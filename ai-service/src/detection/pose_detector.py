from ultralytics import YOLO
import time


class PoseDetector:

    def __init__(self, model_path="yolov8n-pose.pt"):
        self.model = YOLO(model_path)

    def detect(self, frame):

        results = self.model.track(
            frame,
            persist=True,
            classes=[0],
            tracker="bytetrack.yaml",
            verbose=False
        )

        persons = []

        for r in results:
            for box in r.boxes:

                if int(box.cls[0]) != 0:
                    continue

                track_id = int(box.id[0]) if box.id is not None else -1

                kpts = r.keypoints.xy[0].cpu().numpy() if r.keypoints is not None else None

                persons.append({
                    "id": track_id,
                    "bbox": box.xyxy[0].tolist(),
                    "keypoints": kpts
                })

        return persons