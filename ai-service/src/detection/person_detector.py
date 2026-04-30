from ultralytics import YOLO
import time


class PersonDetector:

    def __init__(self, model_manager, model_name, log_interval=30):
        self.model = model_manager.get_model(model_name)
        self.log_interval = log_interval
        self.frame_count = 0

    def person_detect(self, frame):

        start_time = time.time()

        # 🚀 启用 ByteTrack tracking
        results = self.model.track(
            frame,
            classes=[0],          # person
            persist=True,         # 🔥 关键：保持ID连续
            tracker="bytetrack.yaml",
            verbose=False
        )

        inference_time = (time.time() - start_time) * 1000

        persons = []

        for r in results:
            for box in r.boxes:

                cls = int(box.cls[0])

                if cls == 0:

                    x1, y1, x2, y2 = box.xyxy[0]

                    # 🚀 ByteTrack ID
                    track_id = int(box.id[0]) if box.id is not None else -1

                    persons.append({
                        "id": track_id,
                        "x": int(x1),
                        "y": int(y1),
                        "w": int(x2 - x1),
                        "h": int(y2 - y1),
                    })

        self.frame_count += 1
        if self.frame_count % self.log_interval == 0:
            print(f"[ByteTrack] {len(persons)} persons | {inference_time:.1f}ms")

        return persons