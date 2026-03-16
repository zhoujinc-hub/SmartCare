# SmartCare -- AI识别模块指南


## 1.项目启动

- 先确认python版本为3.11
- 再创建虚拟环境：python -m venv venv
- 激活虚拟环境：source venv/bin/activate
- 安装依赖：pip install -r requirements.txt
- 启动项目：python run.py
## 2.核心功能

1. 跌倒检测

- 参考文档：[AI_docs/fall_detection_project.md](AI_docs/fall_detection_project.md)



## 3.项目结构

```
ai-service
│
├── models/                     # AI 模型
│   ├── yolov8n.pt
│   └── pose_model/
│
├── src/
│   │
│   ├── camera/                 # 视频输入
│   │   └── camera.py
│   │
│   ├── detection/              # 人体检测
│   │   └── person_detector.py
│   │
│   ├── tracking/               # 目标跟踪（第二阶段）
│   │   └── tracker.py
│   │
│   ├── pose/                   # 姿态识别（第二阶段）
│   │   └── pose_estimator.py
│   │
│   ├── fall_detection/         # 跌倒检测逻辑
│   │   └── fall_detector.py
│   │
│   ├── events/                 # 事件发送
│   │   └── event_sender.py
│   │
│   ├── utils/                  # 工具类
│   │   ├── draw.py
│   │   └── config_loader.py
│   │
│   └── main.py                 # 程序入口
│
├── config/
│   └── config.yaml
│
├── tests/                      # 测试
│   └── test_detector.py
│
├── requirements.txt
│
├── README.md
│
└── run.py                      # 启动脚本
```