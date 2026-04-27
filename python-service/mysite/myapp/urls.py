"""
URL configuration for mysite project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/5.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.urls import path
from rest_framework.routers import SimpleRouter
from .views import BannerView,CollectionView,AreaView,StatisticsView,FaceView,NoticeView,ActivityView
router = SimpleRouter()
router.register('banner', BannerView, 'banner')
router.register('collection', CollectionView, 'collection')
router.register('area', AreaView, 'area')
router.register('statistics', StatisticsView, 'statistics')
router.register('face', FaceView, 'face')
router.register('notice', NoticeView, 'notice')
router.register('activity', ActivityView, 'activity')
from .views import welcome
from . import views
urlpatterns = [
    # http://127.0.0.1:8000/smart/welcome/-->>就能获得图片数据
    path('welcome/', welcome),
    path('api/login/', views.login),
    path('api/get_user_info/', views.get_user_info),
    path('api/bind_elder/', views.bind_elder), # 绑定老人信息
]
urlpatterns += router.urls
