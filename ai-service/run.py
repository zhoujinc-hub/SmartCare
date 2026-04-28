"""SmartCare AI 服务启动入口（FastAPI / Uvicorn）。"""
from __future__ import annotations

import argparse
import os
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent
SRC = ROOT / "src"
sys.path.insert(0, str(SRC))


def main() -> None:
    try:
        from dotenv import load_dotenv

        load_dotenv(ROOT / ".env")
    except ImportError:
        pass

    parser = argparse.ArgumentParser(description="启动 SmartCare AI HTTP 服务")
    parser.add_argument(
        "--host",
        default=os.getenv("HOST", "0.0.0.0"),
        help="监听地址（默认 0.0.0.0，可用环境变量 HOST）",
    )
    parser.add_argument(
        "--port",
        type=int,
        default=int(os.getenv("PORT", "8000")),
        help="端口（默认 8000，可用环境变量 PORT）",
    )
    parser.add_argument(
        "--reload",
        action="store_true",
        help="开发模式：源码变更自动重载",
    )
    args = parser.parse_args()

    import uvicorn

    uvicorn.run(
        "api.app:app",
        host=args.host,
        port=args.port,
        reload=args.reload,
    )


if __name__ == "__main__":
    main()
