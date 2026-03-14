import cv2


class Camera:

    def __init__(self, source=0):
        self.cap = cv2.VideoCapture(source)
        if not self.cap.isOpened():
            raise Exception("Camera not opened")

    def read(self):
        # 是否读取成功,图像数组
        ret, frame = self.cap.read()
        return ret, frame

    def release(self):
        self.cap.release()