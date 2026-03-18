# 导入必要的模块和依赖
import os
from typing import Any, Dict, Optional

import requests
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

# 创建 FastAPI 应用实例，配置应用标题
app = FastAPI(title="SmartCare AI Minimal API")

# 从环境变量读取 Spring Boot 后端 URL，默认值为本地 8080 端口
SPRING_BOOT_URL = os.getenv("SPRING_BOOT_URL", "http://localhost:8080/api/ai/ingest")
# 从环境变量读取请求超时时间（秒），默认 5 秒
SPRING_BOOT_TIMEOUT = float(os.getenv("SPRING_BOOT_TIMEOUT", "5"))


# 定义转发请求的数据模型
class ForwardRequest(BaseModel):
    data: Dict[str, Any]  # 要转发的数据内容
    headers: Optional[Dict[str, str]] = None  # 可选的 HTTP 请求头


# 健康检查接口，用于服务状态监测
@app.get("/health")
def health() -> Dict[str, str]:
    return {"status": "ok"}


# 请求转发接口，将接收到的请求转发到 Spring Boot 后端
@app.post("/forward")
def forward(payload: ForwardRequest) -> Dict[str, Any]:
    try:
        # 向 Spring Boot 发送 POST 请求
        resp = requests.post(
            SPRING_BOOT_URL,
            json=payload.data,
            headers=payload.headers,
            timeout=SPRING_BOOT_TIMEOUT,
        )
    except requests.RequestException as exc:
        # 捕获所有请求异常，抛出 502 错误
        raise HTTPException(status_code=502, detail=f"Failed to reach Spring Boot: {exc}")

    # 根据响应头的 Content-Type 解析返回内容
    content_type = resp.headers.get("content-type", "")
    if "application/json" in content_type:
        body: Any = resp.json()
    else:
        body = resp.text

    # 返回包含状态和 Spring Boot 响应的结果
    return {
        "status": "ok",
        "spring_status": resp.status_code,  # Spring Boot 返回的状态码
        "spring_body": body,  # Spring Boot 返回的内容
    }
