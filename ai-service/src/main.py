import argparse
import cv2

from camera.camera import Camera
from detection.model_manager import ModelManager
from detection.person_detector import PersonDetector
from fall_detection.fall_detector import FallDetector
from utils.draw import draw_person


def run(model_name: str, source=0):

    camera = Camera(source=source)
    model_manager = ModelManager()
    detector = PersonDetector(model_manager, model_name)
    fall_detector = FallDetector()
    #检测是否正常工作
    while True:

        ret, frame = camera.read()
        #检测是否获取到图片
        if not ret:
            break
        #返回人体框的x,y
        persons = detector.person_detect(frame)

        for p in persons:
            fall_condition, ratio, fall_frame_count= fall_detector.fall_detect(p)
            draw_person(frame, p, fall_condition, ratio, fall_frame_count)

        cv2.imshow("SmartCare AI", frame)

        if cv2.waitKey(1) == 27:
            break

    camera.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--model", required=True, help="Model file name under ./models")
    parser.add_argument("--source", default="0", help="Camera source index or video path")
    args = parser.parse_args()
    source = int(args.source) if str(args.source).isdigit() else args.source
    run(args.model, source)
