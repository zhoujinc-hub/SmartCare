from django.shortcuts import render
from .models import Welcome
from django.http import JsonResponse
from rest_framework.viewsets import GenericViewSet
from rest_framework.mixins import ListModelMixin,DestroyModelMixin,CreateModelMixin
from rest_framework.response import Response
from .models import Banner, Notice
from .serializers import BannerSerializer, NoticeSerializer,CollectionSerializer
from .serializers import CollectionSaveSerializer
#广告接口
def welcome(request):
    res = Welcome.objects.all().order_by('-order').first()
    img = request.build_absolute_uri('/media/' + str(res.img))
    return JsonResponse({'code': 100, 'msg': '成功', 'result': img})
#轮播图接口
class BannerView(GenericViewSet, ListModelMixin):
    queryset = Banner.objects.filter(is_delete=False).order_by('order')[:3]
    serializer_class = BannerSerializer

    def list(self, request, *args, **kwargs):
        res = super().list(request, *args, **kwargs)
        # 获取最后一条通知
        notice = Notice.objects.all().order_by('-create_time').first()
        serializer = NoticeSerializer(instance=notice)

        return Response({'code': 100, 'msg': '成功', 'banner': res.data, 'notice': serializer.data})
# 信息采集接口->登录用户当天采集的所有数据->如果未登录->显示当天采集的所有数据
from .models import Collection
from datetime import datetime
class CollectionView(GenericViewSet,ListModelMixin,DestroyModelMixin,CreateModelMixin):
    # 查出当天的--》没过滤当前用户
    queryset = Collection.objects.all().filter(create_time__gte=datetime.now().date())
    serializer_class = CollectionSerializer

    def get_serializer_class(self):
        if self.action == 'create':
            return CollectionSaveSerializer
        else:
            return CollectionSerializer

    def list(self, request, *args, **kwargs):
        res = super().list(request, *args, **kwargs)
        today_count = len(self.get_queryset())
        return Response({'code': 100, 'msg': '成功', 'result': res.data, 'today_count': today_count})

    ## 删除人脸
    def destroy(self, request, *args, **kwargs):
        from libs.baidu_ai import BaiDuFace
        instance = self.get_object()
        # 百度ai中删除
        baidu = BaiDuFace()
        res = baidu.delete(instance.name_pinyin, face_token=instance.face_token)
        print(res)
        self.perform_destroy(instance)
        return Response()
###当前用户负责的网格##############
from .models import Area
from .serializers import AreaSerializer
class AreaView(GenericViewSet,ListModelMixin):
    queryset = Area.objects.all()
    serializer_class = AreaSerializer
from django.db.models import Count
from django.db.models.functions import Trunc
from .models import Collection
from .serializers import StatisticsListSerializer


class StatisticsView(GenericViewSet, ListModelMixin):
    # 做个分组
    queryset = Collection.objects.annotate(date=Trunc('create_time', 'day')).values('date').annotate(
        count=Count('id')).values('date', 'count')
    serializer_class = StatisticsListSerializer
#人脸检测接口##
from libs.baidu_ai import BaiDuFace
from .serializers import CollectionSaveSerializer
from rest_framework.exceptions import APIException

class FaceView(GenericViewSet):
    def create(self, request, *args, **kwargs):
        # 1 取出前端传入的人脸照片
        avatar_object = request.data.get('avatar')
        if not avatar_object:
            return Response({'code': 103, 'msg': '请正常提交人脸'})

        # 2 使用百度人脸库搜索
        ai = BaiDuFace()
        res = ai.search(avatar_object)

        if res.get('error_code') == 0:
            user_list = res.get('result', {}).get('user_list', [])
            if not user_list:
                return Response({'code': 102, 'msg': '未匹配到任何人脸'})

            user_id = user_list[0].get('user_id')
            score = int(user_list[0].get('score'))

            # 3 先查本地采集表
            user = Collection.objects.filter(name_pinyin=user_id).first()

            if user:
                # 本地已有记录
                return Response({'code': 100, 'msg': '匹配成功', 'name': user.name, 'score': score})
            else:
                # 本地没有记录，尝试从百度人脸库获取用户信息
                user_info = ai.get_user_info(user_id)  # 需要在 BaiDuFace 中实现这个方法
                if user_info:
                    # 自动保存到本地采集表
                    serializer = CollectionSaveSerializer(data={
                        'name': user_info.get('name'),
                        'avatar': avatar_object,  # 前端上传的图片
                        'area': None  # 如果百度库没有区域信息，可以先设为 None
                    })
                    if serializer.is_valid():
                        serializer.save()
                        return Response({
                            'code': 100,
                            'msg': '匹配成功（已自动同步到本地采集表）',
                            'name': user_info.get('name'),
                            'score': score
                        })
                    else:
                        raise APIException(f'自动同步失败: {serializer.errors}')
                else:
                    return Response({
                        'code': 101,
                        'msg': '百度库有记录，但未获取到用户信息，请手动采集'
                    })
        else:
            return Response({'code': 102, 'msg': '该人员不是咱们社区人员，请注意'})
from .models import Notice
from .serializers import NoticeSerializer
class NoticeView(GenericViewSet,ListModelMixin):
    queryset =Notice.objects.all().order_by('create_time')
    serializer_class = NoticeSerializer
from .models import Activity
from .serializers import ActivitySerializer
class ActivityView(GenericViewSet, ListModelMixin):
    queryset = Activity.objects.all().order_by('date')
    serializer_class = ActivitySerializer


from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.db import IntegrityError
import json
from .models import User, Elder, Relation


# 模拟登录接口（实际需加密密码，这里简化）
@csrf_exempt  # 小程序POST请求需关闭CSRF
def login(request):
    if request.method != 'POST':
        return JsonResponse({'code': -1, 'msg': '仅支持POST请求'})

    data = json.loads(request.body)
    username = data.get('username')
    password = data.get('password')

    try:
        user = User.objects.get(username=username, password=password, status=1)
        # 模拟生成token（实际用JWT/SESSION）
        user_info = {
            'user_id': user.user_id,
            'username': user.username,
            'real_name': user.real_name or username,
            'phone': user.phone
        }
        return JsonResponse({
            'code': 0,
            'msg': '登录成功',
            'data': user_info
        })
    except User.DoesNotExist:
        return JsonResponse({'code': -1, 'msg': '用户名/密码错误'})


# 获取当前用户信息（验证登录态）
@csrf_exempt
def get_user_info(request):
    if request.method != 'POST':
        return JsonResponse({'code': -1, 'msg': '仅支持POST请求'})

    data = json.loads(request.body)
    user_id = data.get('user_id')  # 小程序传递登录后的user_id

    try:
        user = User.objects.get(user_id=user_id, status=1)
        return JsonResponse({
            'code': 0,
            'data': {
                'real_name': user.real_name or user.username,
                'user_id': user.user_id
            }
        })
    except User.DoesNotExist:
        return JsonResponse({'code': -1, 'msg': '未登录/用户不存在'})


# 绑定老人信息接口
@csrf_exempt
def bind_elder(request):
    if request.method != 'POST':
        return JsonResponse({'code': -1, 'msg': '仅支持POST请求'})

    data = json.loads(request.body)
    # 必传参数
    user_id = data.get('user_id')
    elder_name = data.get('elder_name')
    elder_age = data.get('elder_age')
    elder_gender = data.get('elder_gender')
    relationship = data.get('relationship')  # 与用户的关系

    if not all([user_id, elder_name]):
        return JsonResponse({'code': -1, 'msg': '老人姓名和用户ID不能为空'})

    try:
        # 1. 创建/查询老人信息
        elder, created = Elder.objects.get_or_create(
            name=elder_name,
            defaults={
                'age': elder_age,
                'gender': elder_gender,
                'address': data.get('address'),
                'health_notes': data.get('health_notes')
            }
        )
        # 2. 绑定用户-老人关系
        Relation.objects.create(
            user_id=user_id,
            elder_id=elder.elder_id,
            relationship=relationship
        )
        return JsonResponse({'code': 0, 'msg': '绑定老人信息成功'})
    except IntegrityError:
        return JsonResponse({'code': -1, 'msg': '已绑定该老人，请勿重复绑定'})
    except Exception as e:
        return JsonResponse({'code': -1, 'msg': f'绑定失败：{str(e)}'})