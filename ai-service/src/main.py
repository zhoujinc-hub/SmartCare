import argparse
import cv2
import time
import os
import json
from datetime import datetime
from collections import deque
import threading

import ratio
import requests
from sympy.printing.tree import print_node

from camera.camera import Camera
from detection.model_manager import ModelManager
from detection.person_detector import PersonDetector
from fall_detection.fall_detector import FallDetector
from utils.draw import draw_person
from detection.pose_detector import PoseDetector
from fall_detection.medical_fall_engine import MedicalFallEngine


def run(source=0, camera_id=1):
    last_positions = {}
    still_threshold = 10  # 像素移动阈值
    still_frames_required = 20  # ~1秒
    still_counter = {}
    triggered_persons = {}
    cooldown = 10  # 秒

    def send_to_ai_gateway(event):
        def task():
            url = "http://localhost:8080/api/ai/ingest"

            try:
                resp = requests.post(
                    url,
                    json=event,
                    timeout=3
                )

                print("📡 AI Gateway成功:", resp.status_code)

            except Exception as e:
                print("❌ Gateway失败:", e)

        threading.Thread(target=task).start()

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

    # ===== 视频缓冲区（存前5秒）=====
    buffer_seconds = 5
    fps = 20  # 估算摄像头帧率（可以调）
    frame_buffer = deque(maxlen=buffer_seconds * fps)

    def save_video_async(frames, filename, fps):
        def task():
            os.makedirs("data/videos", exist_ok=True)

            h, w, _ = frames[0].shape
            path = f"data/videos/{filename}.mp4"

            fourcc = cv2.VideoWriter_fourcc(*"mp4v")
            out = cv2.VideoWriter(path, fourcc, fps, (w, h))

            for f in frames:
                out.write(f)

            out.release()
            print("✅ 视频保存完成:", path)

        t = threading.Thread(target=task)
        t.start()

    camera = Camera(source=source)
    model_manager = ModelManager()
    detector = PoseDetector("yolov8n-pose.pt")
    fall_engine = MedicalFallEngine()
    #检测是否正常工作
    while True:

        ret, frame = camera.read()
        frame_buffer.append(frame.copy())
        #检测是否获取到图片
        if not ret:
            break
        #返回人体框的x,y
        persons = detector.detect(frame)

        for p in persons:

            fall_condition, angle = fall_engine.process(p)

            # 🔥 可视化（建议加）
            bbox = p.get("bbox", None)
            if bbox is None:
                continue

            x1, y1, x2, y2 = map(int, bbox)
            cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)

            cv2.putText(frame, f"angle:{angle:.1f}", (x1, y1 - 10),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 0), 2)



            if fall_condition:

                now = time.time()

                if now - last_trigger_time > cooldown:

                    last_trigger_time = now

                    print("🚨 医疗级跌倒检测！（全局触发）")

                    # ===== 原来的逻辑 =====

                    print("⚠️ 检测到摔跤！开始保存视频...")

                    pre_frames = list(frame_buffer)


                    post_frames = []
                    start_time = time.time()

                    while time.time() - start_time < 5:
                        ret2, frame2 = camera.read()
                        if not ret2:
                            break
                        post_frames.append(frame2)

                    all_frames = pre_frames + post_frames

                    filename = datetime.now().strftime("%Y%m%d_%H%M%S")

                    save_video_async(all_frames, filename, fps)

                    video_path = f"data/videos/{filename}.mp4"

                    screenshot_path = save_screenshot(frame)

                    confidence = float(max(0.5, min(1.0, 1 - float(angle) / 90)))

                    event = {
                        "camera_id": camera_id,
                        "elder_id": None,
                        "elder_name": None,
                        "is_registered": 0,

                        "fall_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
                        "detect_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),

                        "video_path": video_path,
                        "screenshot_path": screenshot_path,

                        "confidence": confidence,
                        "angle": float(angle)
                    }

                    save_event(event)
                    send_to_ai_gateway(event)

                    print("✅ 已上报一次:", video_path)

        cv2.imshow("SmartCare AI", frame)

        if cv2.waitKey(1) == 27:
            break

    camera.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", default="0", help="Camera source index or video path")
    parser.add_argument("--camera_id", default=1, type=int)
    args = parser.parse_args()
    source = int(args.source) if str(args.source).isdigit() else args.source
    run( source, args.camera_id)
