import cv2

from camera.camera import Camera
from detection.person_detector import PersonDetector
from fall_detection.fall_detector import FallDetector
from utils.draw import draw_person

def run():

    camera = Camera()
    detector = PersonDetector()
    fall_detector = FallDetector()
    #检测是否正常工作
    while True:

        ret, frame = camera.read()
        #检测是否获取到图片
        if not ret:
            break
        #返回人体框的x,y
        persons = detector.detect(frame)

        for p in persons:
            fall_condition, ratio = fall_detector.detect(p)
            draw_person(frame, p, fall_condition, ratio)

        cv2.imshow("SmartCare AI", frame)

        if cv2.waitKey(1) == 27:
            break

    camera.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    run()