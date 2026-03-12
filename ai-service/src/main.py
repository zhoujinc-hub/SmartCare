import cv2

from camera.camera import Camera
from detection.person_detector import PersonDetector
from utils.draw import draw_person_box


def run():

    camera = Camera()
    detector = PersonDetector()

    while True:

        ret, frame = camera.read()

        if not ret:
            break

        persons = detector.detect(frame)

        for p in persons:
            draw_person_box(frame, p)

        cv2.imshow("SmartCare AI", frame)

        if cv2.waitKey(1) == 27:
            break

    camera.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    run()