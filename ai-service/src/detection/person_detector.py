#识别人体

from ultralytics import YOLO


class PersonDetector:

    def __init__(self, model_path="models/yolov8n.pt"):
        self.model = YOLO(model_path)

    def detect(self, frame):
        #只检测 person
        results = self.model(frame, classes=[0])

        persons = []

        for r in results:
            for box in r.boxes:

                cls = int(box.cls[0])

                # 0 是 person
                if cls == 0:

                    x1, y1, x2, y2 = box.xyxy[0]

                    persons.append({
                        "x": int(x1),
                        "y": int(y1),
                        "w": int(x2 - x1),
                        "h": int(y2 - y1),

                    })

        return persons