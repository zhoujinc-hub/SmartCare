# SmartCare 项目框架文档

> 智慧养老监护系统 — 基于 AI 视觉的社区/家庭老年人安全监控平台

---

## 一、项目概述

SmartCare 是一个面向社区和家庭场景的智慧养老监护系统，核心功能是通过摄像头实时监控老年人活动，利用 AI 跌倒检测算法自动识别跌倒事件并及时报警通知家属和管理人员。

**技术栈总览：**

| 模块 | 技术栈 |
|------|--------|
| 前端 | Vue 3 + TypeScript + Vite + Pinia + Vue Router + Element Plus + ECharts |
| 后端 | Spring Boot 3.0.5 + MyBatis-Plus + MySQL 8.0 + WebSocket |
| AI 服务 | FastAPI + Uvicorn + YOLOv8 (ultralytics) + OpenCV + ByteTrack |
| 数据库 | MySQL 8.0 (InnoDB, utf8mb4) |
| 部署 | Docker Compose |

**端口分配：**

| 服务 | 端口 |
|------|------|
| 前端开发服务器 | 5173 |
| 前端 Docker | 8081 |
| 后端 API | 8080 |
| AI 服务 | 8000 |
| MySQL | 3306 |

---

## 二、项目目录结构

```
SmartCare/
├── frontend/                  # 前端 (Vue 3)
├── backend/                   # 后端 (Spring Boot)
├── ai-service/                # AI 服务 (FastAPI + YOLOv8)
├── database/                  # 数据库脚本 (MySQL)
├── docs/                      # 项目文档
├── .github/                   # CI/CD 配置
├── docker-compose.yml         # Docker 编排
├── .env                       # 环境变量
└── README.md
```

---

## 三、前端模块 (frontend/)

**技术栈：** Vue 3 + TypeScript + Vite + Pinia + Vue Router 4 + Element Plus + ECharts + Axios + flv.js + Hls.js

### 3.1 目录结构

```
frontend/
├── src/
│   ├── api/                   # API 请求层
│   │   ├── alert.ts           # 告警日志 API
│   │   ├── camera.ts          # 摄像头管理 API
│   │   ├── elder.ts           # 老人管理 API
│   │   ├── fall-event.ts      # 跌倒事件 API
│   │   ├── map.ts             # 报警地图 API
│   │   ├── relation.ts        # 家属-老人关系 API
│   │   └── user.ts            # 用户认证 API
│   ├── assets/                # 静态资源 (CSS, 图片, 字体)
│   ├── components/            # 通用组件
│   ├── composables/           # 组合式函数 (useAlarmWebSocket, useCameraStream)
│   ├── layouts/               # 布局组件 (AdminLayout, FamilyLayout)
│   ├── router/                # 路由配置 (adminRoutes, familyRoutes, authRoutes)
│   ├── stores/                # Pinia 状态管理
│   ├── styles/                # 全局样式
│   ├── types/                 # TypeScript 类型定义
│   ├── utils/                 # 工具函数 (request封装, 流处理)
│   └── views/                 # 页面视图
│       ├── admin/             # 管理员页面
│       │   ├── HomeView.vue           # 仪表盘首页
│       │   ├── AlarmMapView.vue       # 报警地图
│       │   ├── CameraListView.vue     # 摄像头管理
│       │   ├── CameraPlayerView.vue   # 摄像头播放
│       │   ├── ElderListView.vue      # 老人信息管理
│       │   ├── EventListView.vue      # 跌倒事件列表
│       │   ├── FamilyListView.vue     # 家属-老人关系管理
│       │   └── AlertLogListView.vue   # 告警日志
│       ├── family/            # 家属页面
│       │   ├── DashboardView.vue      # 家属仪表盘
│       │   ├── FamilyElderView.vue    # 绑定老人信息
│       │   └── FamilyFallEventView.vue # 跌倒事件记录
│       └── auth/              # 认证页面
│           ├── LoginView.vue          # 登录
│           ├── RegisterView.vue       # 注册
│           └── ResetPasswordView.vue  # 重置密码
├── index.html
├── vite.config.ts             # Vite 配置 (代理到 localhost:8080)
├── tsconfig.json
└── package.json
```

### 3.2 前端路由体系

前端采用双布局体系，根据用户角色区分：

**管理员路由 (`/admin/*`)：**
| 路径 | 页面 | 功能 |
|------|------|------|
| `/admin/home` | HomeView | 系统仪表盘 |
| `/admin/alarm-map` | AlarmMapView | 实时报警地图 |
| `/admin/cameras` | CameraListView | 摄像头列表管理 |
| `/admin/cameras/:id/player` | CameraPlayerView | 摄像头实时播放 |
| `/admin/elders` | ElderListView | 老人信息管理 |
| `/admin/events` | EventListView | 跌倒事件列表 |
| `/admin/families` | FamilyListView | 家属-老人关系管理 |
| `/admin/alerts` | AlertLogListView | 告警日志 |

**家属路由 (`/family/*`)：**
| 路径 | 页面 | 功能 |
|------|------|------|
| `/family/dashboard` | DashboardView | 家属仪表盘 |
| `/family/elders` | FamilyElderView | 绑定老人查看 |
| `/family/events` | FamilyFallEventView | 跌倒事件记录 |

**认证路由：**
| 路径 | 页面 |
|------|------|
| `/login` | LoginView |
| `/register` | RegisterView |
| `/reset-password` | ResetPasswordView |

### 3.3 前端核心功能模块

- **用户认证：** 登录、注册、短信验证码、密码重置
- **实时报警：** WebSocket 连接 (`useAlarmWebSocket`) 实时接收跌倒告警
- **视频播放：** 支持 FLV / HLS 流 (`useCameraStream`, `flv.js`, `hls.js`)
- **数据可视化：** ECharts 图表展示统计数据
- **地图报警：** 报警事件地图可视化

---

## 四、后端模块 (backend/)

**技术栈：** Spring Boot 3.0.5 + MyBatis-Plus 3.5.12 + Knife4j 4.1.0 + MySQL + WebSocket

**基础包名：** `com.smartcare`

### 4.1 目录结构

```
backend/src/main/java/com/smartcare/
├── SmartcareApplication.java          # 启动入口
├── common/                            # 公共模块
│   ├── Result.java                    # 统一响应封装
│   ├── ResultCodeEnum.java            # 状态码枚举
│   ├── UserContext.java               # ThreadLocal 用户上下文
│   ├── enums/                         # 业务枚举
│   │   ├── AlertLevelEnum.java        # 告警级别
│   │   ├── CameraOnlineStatusEnum.java # 摄像头在线状态
│   │   ├── ElderStatusEnum.java       # 老人状态
│   │   ├── FallEventStatusEnum.java   # 跌倒事件状态
│   │   ├── GenderEnum.java            # 性别
│   │   ├── SendStatusEnum.java        # 发送状态
│   │   └── UserStatusEnum.java        # 用户状态
│   └── exception/
│       ├── BusinessException.java      # 业务异常
│       └── GlobalExceptionHandler.java # 全局异常处理
├── config/                            # 配置类
│   ├── Knife4jConfiguration.java      # API 文档配置 (OpenAPI 3)
│   ├── MybatisPlusConfiguration.java  # MyBatis-Plus 分页插件
│   ├── WebConfig.java                 # Web 配置 (拦截器)
│   └── WebSocketConfig.java           # WebSocket 配置 (/ws/alarm)
├── controller/                        # 控制器层 (8个)
├── dto/                               # 数据传输对象
│   ├── alert/                         # 告警相关 DTO
│   ├── camera/                        # 摄像头相关 DTO
│   ├── elder/                         # 老人相关 DTO
│   ├── fall/                          # 跌倒事件相关 DTO
│   ├── relation/                      # 关系相关 DTO
│   └── user/                          # 用户相关 DTO
├── entity/                            # 实体类 (7个)
├── interceptor/
│   └── LoginInterceptor.java          # 登录拦截器 (token 校验)
├── mapper/                            # MyBatis-Plus Mapper 接口 (7个)
├── service/                           # 服务层接口 (8个)
│   └── impl/                          # 服务层实现 (8个)
├── utils/
│   ├── HttpUtils.java                 # HTTP 工具
│   └── VerifyCodeUtil.java            # 验证码工具
├── vo/                                # 视图对象
│   ├── AlarmMapVO.java                # 报警地图 VO
│   ├── Page/PageVo.java               # 分页 VO
│   ├── alert/ camera/ elder/ fall/ relation/ user/
└── websocket/
    └── AlarmWebSocketHandler.java     # 报警 WebSocket 处理器
```

### 4.2 实体与数据库表映射

| 实体类 | 数据库表 | 说明 |
|--------|----------|------|
| `Users` | `users` | 系统用户（管理员/家属） |
| `Elders` | `elders` | 老人信息 |
| `Cameras` | `cameras` | 监控摄像头 |
| `FallEvents` | `fall_events` | 跌倒事件 |
| `AlertLogs` | `alert_logs` | 告警通知记录 |
| `Relations` | `relations` | 家属-老人关系 |

### 4.3 API 接口汇总 (共 24 个 REST 端点 + 1 个 WebSocket)

#### 用户模块 `/api/user`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/login` | 用户登录 |
| POST | `/api/user/send-code` | 发送短信验证码 |
| POST | `/api/user/register` | 用户注册 |
| POST | `/api/user/resetPassword` | 重置密码 |

#### 老人管理 `/api/elders`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/elders/list` | 老人列表（分页） |
| POST | `/api/elders/add` | 添加老人 |
| PUT | `/api/elders/update` | 更新老人信息 |
| DELETE | `/api/elders/delete/{elderId}` | 删除老人 |
| GET | `/api/elders/detail/{elderId}` | 老人详情 |

#### 摄像头管理 `/api/cameras`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/cameras/list` | 摄像头列表（分页） |
| POST | `/api/cameras/add` | 添加摄像头 |
| PUT | `/api/cameras/update` | 更新摄像头 |
| DELETE | `/api/cameras/delete/{cameraId}` | 删除摄像头 |
| GET | `/api/cameras/detail/{cameraId}` | 摄像头详情 |
| POST | `/api/cameras/refreshStatus` | 刷新摄像头在线状态 |

#### 跌倒事件 `/api/fallEvents`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/fallEvents/list` | 跌倒事件列表（分页） |
| GET | `/api/fallEvents/detail/{eventId}` | 事件详情 |
| PUT | `/api/fallEvents/handle/{eventId}` | 处理跌倒事件 |
| PUT | `/api/fallEvents/notes/{eventId}` | 保存事件备注 |

#### 告警日志 `/api/alertLogs`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/alertLogs/list` | 告警日志列表（分页） |
| GET | `/api/alertLogs/{alertId}` | 告警详情 |
| PUT | `/api/alertLogs/handle` | 处理告警 |
| POST | `/api/alertLogs/{alertId}/resend` | 重新发送告警通知 |

#### 家属-老人关系 `/api/relations`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/relations/list` | 关系列表（分页） |

#### 报警地图 `/api/alarm-map`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/alarm-map/list` | 地图报警点列表 |
| PUT | `/api/alarm-map/{eventId}/status` | 更新报警事件状态 |
| POST | `/api/alarm-map/{eventId}/push` | 手动推送报警（调试用） |

#### 家属端接口 `/api/family/elder`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/family/elder/list/{userId}` | 家属绑定的老人列表 |
| POST | `/api/family/elder/add/{userId}` | 家属添加并绑定老人 |
| GET | `/api/family/elder/{userId}/{elderId}/fallEvents` | 查看老人跌倒事件 |
| POST | `/api/family/elder/{userId}/{elderId}/delete` | 家属端删除老人 |

#### WebSocket

| 协议 | 路径 | 说明 |
|------|------|------|
| WS | `/ws/alarm` | 实时报警推送 |

---

## 五、AI 服务模块 (ai-service/)

**技术栈：** FastAPI + Uvicorn + YOLOv8 (ultralytics) + OpenCV + ByteTrack

### 5.1 目录结构

```
ai-service/
├── run.py                       # 启动入口 (Uvicorn, port 8000)
├── requirements.txt             # Python 依赖
├── .env                         # 环境变量 (SPRING_BOOT_URL)
├── models/
│   └── yolov8n.pt               # YOLOv8 检测模型
├── yolov8n-pose.pt              # YOLOv8 姿态估计模型
├── src/
│   ├── main.py                  # 独立 CLI 测试入口 (cv2 显示循环)
│   ├── api/
│   │   └── main_api.py          # FastAPI 应用 (HTTP + WebSocket)
│   ├── camera/
│   │   └── camera.py            # OpenCV VideoCapture 封装
│   ├── detection/
│   │   ├── model_manager.py     # YOLO 模型加载/缓存
│   │   ├── person_detector.py   # YOLO 人体检测 + ByteTrack
│   │   └── pose_detector.py     # YOLOv8 姿态估计 + ByteTrack
│   ├── fall_detection/
│   │   ├── fall_detector.py     # 传统边界框比例检测（遗留）
│   │   ├── medical_fall_engine.py    # 医疗级跌倒检测引擎（编排器）
│   │   ├── medical_pose_analyzer.py  # 姿态角度分析
│   │   └── temporal_buffer.py   # 滑动窗口时序平滑
│   ├── schema/
│   │   └── ai_result.py         # Pydantic 数据模型
│   ├── service/
│   │   ├── event_dispatcher.py  # 事件分发器 (POST 到后端)
│   │   └── rule_engine.py       # 业务规则引擎
│   └── utils/
│       └── draw.py              # OpenCV 绘图工具
├── data/                        # 运行时数据 (截图/视频)
├── logs/                        # 事件日志
├── video/                       # 测试视频
└── tests/                       # 测试目录
```

### 5.2 AI 跌倒检测流水线

```
摄像头/视频源
    │
    ▼
PoseDetector (YOLOv8-pose + ByteTrack)
    │  输出: [{id, bbox, keypoints(17个COCO关键点)}, ...]
    ▼
MedicalFallEngine (编排器)
    │
    ├── MedicalPoseAnalyzer
    │     计算肩-臀-踝三点夹角
    │     角度 < 110° → 初步判定跌倒
    │
    └── TemporalBuffer (30帧滑动窗口)
          连续跌倒帧 >= 5  OR  加权比 > 0.5
          → 最终确认跌倒
    │
    ▼
RuleEngine (置信度 >= 0.6 且事件为 "fall_detected")
    │
    ▼
EventDispatcher → POST 到后端 /api/ai/ingest
```

### 5.3 AI 服务 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/health` | 健康检查，返回 `{"status": "ok"}` |
| POST | `/forward` | 接收检测数据，转发到后端并广播 WebSocket |
| WS | `/ws` | WebSocket 端点，实时推送跌倒告警 |

---

## 六、数据库设计 (database/)

**数据库：** MySQL 8.0，库名 `smartcare_db`，字符集 utf8mb4

### 6.1 目录结构

```
database/
├── init/
│   └── 01-smartcare_db.sql              # 初始化建表脚本
├── migrations/
│   ├── 02-smartcare_db_with_data.sql     # 测试数据（用户/老人/摄像头/事件/告警）
│   ├── 03-smartcare_db_add50_cameras_data.sql  # 新增50个摄像头
│   └── 04-merge_sqlite_into_mysql_no_userinfo.sql  # Django SQLite 数据迁移
├── db-reset.sh                           # 数据库重置脚本
├── db-migrate.sh                         # 增量迁移脚本
└── READEME.md
```

### 6.2 数据库表结构

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `users` | 系统用户 | user_id, username, password, real_name, phone, user_type(1=管理员, 2=家属), status |
| `elders` | 老人信息 | elder_id, name, age, gender(0=女, 1=男), address, health_notes |
| `cameras` | 摄像头 | camera_id, camera_type(0=家庭, 1=社区), camera_name, device_serial, stream_url, location_desc, latitude, longitude, status |
| `fall_events` | 跌倒事件 | event_id, camera_id, elder_id, fall_time, detect_time, video_path, screenshot_path, confidence(0-1), status(1=待处理, 2=已处理, 3=误报) |
| `alert_logs` | 告警记录 | alert_id, event_id, recipient_id, recipient_phone, send_method(1=短信, 2=APP, 3=电话), send_status(0=失败, 1=成功, 2=发送中) |
| `relations` | 家属-老人关系 | relation_id, elder_id, user_id, relationship(如"父子""母女") |

### 6.3 外键关系

```
fall_events.camera_id  →  cameras.camera_id
fall_events.elder_id   →  elders.elder_id
fall_events.processed_by → users.user_id
alert_logs.event_id    →  fall_events.event_id  (CASCADE)
alert_logs.recipient_id → users.user_id
relations.elder_id     →  elders.elder_id  (CASCADE)
relations.user_id      →  users.user_id  (CASCADE)
```

### 6.4 测试数据规模

| 数据 | 数量 |
|------|------|
| 用户 | 15（5管理员 + 10家属） |
| 老人 | 20 |
| 家属-老人关系 | 28 |
| 摄像头 | 60 |
| 跌倒事件 | 30 |
| 告警记录 | 50 |

---

## 七、核心业务功能

### 7.1 管理员端功能

| 功能模块 | 说明 |
|----------|------|
| 仪表盘 | 系统概览、统计数据 |
| 报警地图 | 实时跌倒事件地图可视化 |
| 摄像头管理 | 增删改查摄像头，刷新在线状态，实时播放 |
| 老人管理 | 增删改查老人信息 |
| 跌倒事件管理 | 查看事件列表、详情，处理事件（确认/误报），添加备注 |
| 家属关系管理 | 管理家属与老人的绑定关系 |
| 告警日志 | 查看通知记录，重新发送告警 |

### 7.2 家属端功能

| 功能模块 | 说明 |
|----------|------|
| 仪表盘 | 关联老人概况 |
| 我的老人 | 查看绑定的老人信息，添加老人 |
| 跌倒记录 | 查看关联老人的跌倒事件 |

### 7.3 系统功能

| 功能 | 说明 |
|------|------|
| 用户认证 | 登录、注册、短信验证码、密码重置 |
| 实时报警 | WebSocket 双通道：后端 `/ws/alarm` + AI 端 `/ws` |
| AI 跌倒检测 | 姿态角度分析 + 时序平滑，自动触发告警 |
| 告警通知 | 支持短信、APP 推送、电话三种通知方式 |

---

## 八、数据流与系统架构

```
┌──────────┐    视频流    ┌──────────────┐    POST /api/ai/ingest
│  摄像头   │ ──────────→ │  AI 服务      │ ────────────────────────┐
└──────────┘             │  (FastAPI)    │                         │
                         │  YOLOv8检测   │    WS /ws               │
                         │  姿态分析     │ ←── 前端实时告警 ──┐     │
                         │  时序平滑     │                    │     │
                         └──────────────┘                    │     ▼
                                                    ┌────────────────┐
                                                    │   后端服务      │
                                                    │  (Spring Boot)  │
                                                    │  事件处理       │
                                                    │  告警分发       │
                                                    │  WebSocket推送  │
                                                    └───────┬────────┘
                                                            │
                                                            ▼
                                                    ┌────────────────┐
                                                    │   MySQL 数据库  │
                                                    │  smartcare_db   │
                                                    └───────┬────────┘
                                                            │
                                                            ▼
                                                    ┌────────────────┐
                                                    │   前端应用      │
                                                    │  (Vue 3)        │
                                                    │  管理员/家属端  │
                                                    └────────────────┘
```

---

## 九、部署方式

### 方式一：本地启动

启动顺序：MySQL → 后端(8080) → 前端(5173) → AI服务(8000)

```bash
# 1. 数据库
cd database && bash db-reset.sh

# 2. 后端
cd backend && mvn spring-boot:run

# 3. 前端
cd frontend && npm install && npm run dev

# 4. AI 服务
cd ai-service && python run.py
```

### 方式二：Docker 一键部署

```bash
docker compose up -d
```

---

## 十、未来扩展方向

- 长时间无活动检测
- 夜间异常行为检测
- 可穿戴设备集成
- 社区级统计仪表盘
