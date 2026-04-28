import argparse
import cv2
import time
import os
import json
from datetime import datetime

from camera.camera import Camera
from detection.model_manager import ModelManager
from detection.person_detector import PersonDetector
from fall_detection.fall_detector import FallDetector
from utils.draw import draw_person


def run(model_name: str, source=0):
    # ===== 新增：事件保存相关 =====
    last_trigger_time = 0  # 防重复触发

    def save_event(event):
        os.makedirs("data", exist_ok=True)
        with open("data/fall_events.json", "a", encoding="utf-8") as f:
            f.write(json.dumps(event, ensure_ascii=False) + "\n")

    def save_screenshot(frame):
        os.makedirs("data/images", exist_ok=True)
        filename = datetime.now().strftime("%Y%m%d_%H%M%S") + ".jpg"
        path = f"data/images/{filename}"
        cv2.imwrite(path, frame)
        return path

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
            fall_condition, ratio, fall_frame_count = fall_detector.fall_detect(p)

            draw_person(frame, p, fall_condition, ratio, fall_frame_count)

            # ===== 新增：检测到摔倒后触发 =====
            if fall_condition:
                now = time.time()

                # 10秒内只触发一次
                if now - last_trigger_time > 10:
                    last_trigger_time = now

                    print("⚠️ 检测到摔倒！")

                    # 1️⃣ 保存截图
                    screenshot_path = save_screenshot(frame)

                    # 2️⃣ 构建事件
                    event = {
                        "camera_id": 1,
                        "fall_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
                        "confidence": 0.9,
                        "screenshot_path": screenshot_path
                    }

                    # 3️⃣ 保存事件
                    save_event(event)

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
