#识别人体

from ultralytics import YOLO


class PersonDetector:

    def __init__(self, model_manager, model_name, log_interval=30):
        self.model_manager = model_manager
        self.model= self.model_manager.get_model(model_name)
        self.log_interval = log_interval  # 每多少帧输出一次日志
        self.frame_count = 0

    def person_detect(self, frame):
        import time
        start_time = time.time()
        
        #只检测 person
        results = self.model(frame, classes=[0],verbose=False)
        
        inference_time = (time.time() - start_time) * 1000  # 转换为毫秒

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
        
        # 按间隔输出日志
        self.frame_count += 1
        if self.frame_count % self.log_interval == 0:
            print(f"[帧 {self.frame_count}] 检测到 {len(persons)} 个人，推理耗时: {inference_time:.1f}ms")

        return persons