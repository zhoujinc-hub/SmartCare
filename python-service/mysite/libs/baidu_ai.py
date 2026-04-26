from aip import AipFace
import base64
from pypinyin import pinyin, Style, lazy_pinyin

class BaiDuFace:
    # 注册应用有的
    def __init__(self, APP_ID='121374105', API_KEY='P6CDEWhe13TVpf1FDN1Io0EN',SECRET_KEY='sNiDPxg3xtR94RK4ncV23Vjt8rPPf0Ay'):
        self.APP_ID = APP_ID
        self.API_KEY = API_KEY
        self.SECRET_KEY = SECRET_KEY
        self.client = AipFace(self.APP_ID, self.API_KEY, self.SECRET_KEY)

    # 注册人脸
    def add_user(self,file_obj,userId):
        # 把图片转成base64
        image =base64.b64encode(file_obj.read()).decode('utf-8')
        imageType = "BASE64"
        groupId = "100"
        # userId = "dilireba"
        """ 调用人脸注册 """
        # client.addUser(image, imageType, groupId, userId);

        """ 如果有可选参数 """
        options = {}
        options["user_info"] = "这是迪丽热巴"
        options["quality_control"] = "NORMAL"
        options["liveness_control"] = "LOW"
        options["action_type"] = "REPLACE"
        """ 带参数调用人脸注册 """
        res=self.client.addUser(image, imageType, groupId, userId)

        return res


    # 删除人脸
    def delete(self,userId,faceToken):
        groupId = "100"
        # 注册完人脸--》返回的
        """ 调用人脸删除 """
        res=self.client.faceDelete(userId, groupId, faceToken)
        return res

    # 搜索人脸
    def search(self,file_obj):
        image = base64.b64encode(file_obj.read()).decode('utf-8')
        imageType = "BASE64"
        groupIdList = "100"
        """ 调用人脸搜索 """
        res=self.client.search(image, imageType, groupIdList);
        return res

    def name_to_pinyin(self,text):
        style = Style.TONE3
        name_list = lazy_pinyin(text, style=style)
        return ''.join(name_list)

    def get_user_info(self, user_id):
        """
        从百度人脸库获取用户信息
        """
        try:
            res = self.client.getUser(user_id, self.group_id)
            if res.get('error_code') == 0:
                return {
                    'name': res.get('result').get('user_info'),  # 这里可能需要根据百度返回调整
                    'user_id': user_id
                }
            else:
                print(f"获取用户信息失败: {res}")
                return None
        except Exception as e:
            print(f"获取用户信息异常: {e}")
            return None





