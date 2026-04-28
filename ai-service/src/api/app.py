# 导入必要的模块和依赖
import json
import os
from datetime import datetime
from typing import Any, Dict, Optional

import requests
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from dotenv import load_dotenv
load_dotenv()  # 自动读取 .env 文件

# 创建 FastAPI 应用实例，配置应用标题
app = FastAPI(title="SmartCare AI Minimal API")

# 从环境变量读取 Spring Boot 后端 URL，默认值为本地 8080 端口
SPRING_BOOT_URL = os.getenv("SPRING_BOOT_URL", "http://localhost:8080/api/ai/ingest")
# 从环境变量读取请求超时时间（秒），默认 5 秒
SPRING_BOOT_TIMEOUT = float(os.getenv("SPRING_BOOT_TIMEOUT", "5"))

print(f"[INFO] 转发目标: {SPRING_BOOT_URL}")
print(f"[INFO] 超时设置: {SPRING_BOOT_TIMEOUT}秒")


# 定义转发请求的数据模型
class ForwardRequest(BaseModel):
    data: Dict[str, Any]  # 要转发的数据内容
    headers: Optional[Dict[str, str]] = None  # 可选的 HTTP 请求头



def normalize_event(data: dict):
    return {
        "source": "ai-camera",
        "camera_id": data.get("camera_id"),
        "event_type": "fall_detected",
        "payload": data,
        "timestamp": datetime.now().isoformat()
    }
def log_event(event):
    os.makedirs("logs", exist_ok=True)

    with open("logs/events.jsonl", "a", encoding="utf-8") as f:
        f.write(json.dumps(event, ensure_ascii=False) + "\n")
# 健康检查接口，用于服务状态监测
@app.get("/health")
def health() -> Dict[str, str]:
    return {"status": "ok"}


# 请求转发接口，将接收到的请求转发到 Spring Boot 后端
@app.post("/forward")
def forward(payload: ForwardRequest):

    event = normalize_event(payload.data)
    log_event(event)

    try:
        resp = requests.post(
            SPRING_BOOT_URL,
            json=event,
            timeout=SPRING_BOOT_TIMEOUT,
        )

    except requests.RequestException as exc:
        raise HTTPException(status_code=502, detail=str(exc))

    return {
        "status": "ok",
        "forwarded_event": event,
        "spring_status": resp.status_code,
        "spring_body": resp.text
    }
