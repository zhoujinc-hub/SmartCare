# SmartCare 作品安装说明（计算机设计大赛提交版）

## 1. 作品简介

SmartCare 是一套面向社区/家庭养老场景的智能监护系统，包含以下模块：

- `frontend/`：Web 管理平台（Vue 3 + TypeScript + Vite）
- `backend/`：后端服务（Spring Boot + MyBatis-Plus）
- `ai-service/`：AI 跌倒检测与事件转发服务（FastAPI + Uvicorn + YOLO）
- `database/`：MySQL 初始化与迁移脚本

推荐在 Windows 10/11 环境部署，本文档按“从零到可运行”编写，可用于比赛评审复现与现场答辩演示。

## 2. 环境要求

### 2.1 软件版本建议

- JDK：17 及以上
- Maven：3.8 及以上
- Node.js：18 及以上（建议 18/20 LTS）
- Python：3.10 及以上
- MySQL：8.0
- Docker Desktop（可选）：用于一键部署

### 2.2 端口规划（默认）

- 前端开发服务：`5173`
- 前端 Docker 服务：`8081`
- 后端服务：`8080`
- AI 服务：`8000`
- MySQL：`3306`

请确保以上端口未被占用。

## 3. 项目获取与目录说明

1. 获取项目源码并进入根目录：

```bash
git clone <你的仓库地址>
cd SmartCare
```

2. 目录结构（核心）：

```text
SmartCare
├─ frontend
├─ backend
├─ ai-service
├─ database
└─ docs
```

## 4. 方式A：本地分模块安装（推荐答辩演示）

## 4.1 初始化 MySQL 数据库

### 步骤 1：启动 MySQL 服务

确保本机 MySQL 8.0 正常运行，且可使用 `root` 账号执行 SQL。

### 步骤 2：执行初始化脚本

在 MySQL 客户端中执行：

```sql
SOURCE E:/projects/SmartCare/database/init/01-smartcare_db.sql;
```

说明：

- 该脚本会创建 `smartcare_db` 数据库及业务表
- 默认创建应用账号：`smartcare_admin`
- 默认密码：`password`

如需快速导入示例数据，可继续执行：

```sql
SOURCE E:/projects/SmartCare/database/migrations/02-smartcare_db_with_data.sql;
```

### 步骤 3：检查后端数据库配置

后端默认读取 `backend/src/main/resources/application.yml`，其中默认连接：

- 数据库名：`smartcare_db`
- 用户名：`smartcare_admin`
- 密码：`password`
- 端口：`3306`

比赛提交前，建议将数据库地址和密码替换为比赛现场环境，避免使用示例配置。

## 4.2 启动后端服务（Spring Boot）

在项目根目录执行：

```bash
cd backend
mvn clean package -DskipTests
mvn spring-boot:run
```

预期结果：

- 控制台出现 Spring Boot 启动成功日志
- 后端监听 `http://localhost:8080`

可选自检（浏览器访问）：

- `http://localhost:8080`

## 4.3 启动前端服务（Vue + Vite）

新开终端，在项目根目录执行：

```bash
cd frontend
npm install
npm run dev
```

预期结果：

- Vite 启动成功
- 浏览器访问 `http://localhost:5173`

说明：

- 前端默认通过 `/api` 代理访问后端
- `frontend/.env.development` 中可通过 `VITE_API_BASE_URL` 配置后端地址

## 4.4 启动 AI 服务（FastAPI）

新开终端，在项目根目录执行：

```bash
cd ai-service
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
python run.py --host 0.0.0.0 --port 8000
```

预期结果：

- AI 服务启动在 `http://localhost:8000`
- 提供健康检查接口：`GET /health`

可选自检（浏览器访问）：

- `http://localhost:8000/health`

说明：

- AI 服务默认将事件转发到 `http://localhost:8080/api/ai/ingest`
- 如需修改，可在 AI 服务环境变量中设置 `SPRING_BOOT_URL`

## 4.5 联调顺序建议

建议按以下顺序启动，稳定性更高：

1. MySQL
2. Backend（8080）
3. Frontend（5173）
4. AI Service（8000）

## 5. 方式B：Docker 一键部署（快速复现）

项目提供 `docker-compose.yml`，可快速拉起数据库、后端、前端服务。

## 5.1 准备环境变量文件

在项目根目录创建 `.env` 文件（与 `docker-compose.yml` 同级），参考内容：

```env
DB_HOST=db
DB_PORT=3306
DB_NAME=smartcare_db
DB_USER=smartcare_admin
DB_PASSWORD=password
VITE_API_BASE_URL=http://localhost:8080
```

## 5.2 启动容器

在项目根目录执行：

```bash
docker compose up -d --build
```

预期结果：

- `smartcare-db`、`smartcare-backend`、`smartcare-frontend` 容器启动成功
- 首次启动时，数据库会自动执行 `database/init` 下初始化脚本

## 5.3 访问地址

- 前端（Docker）：`http://localhost:8081`
- 后端：`http://localhost:8080`
- MySQL：`localhost:3306`

## 6. 验收与功能检查清单

完成安装后，按以下步骤验收：

1. 前端页面可正常打开（`5173` 或 `8081`）
2. 后端接口可响应（`8080`）
3. 数据库中可查询到业务表（如 `users`、`elders`、`fall_events`）
4. AI 服务健康检查返回 `{"status":"ok"}`（`8000/health`）
5. AI 服务触发事件后，后端可接收 `/api/ai/ingest` 请求

## 7. 常见问题与排查

## 7.1 后端启动失败（数据库连接错误）

排查要点：

- MySQL 是否启动
- `application.yml` 的地址、端口、用户名、密码是否正确
- `smartcare_db` 是否已初始化
- 3306 端口是否被占用或被防火墙拦截

## 7.2 前端页面空白或接口 404

排查要点：

- 是否先启动后端（8080）
- `frontend/.env.development` 中 `VITE_API_BASE_URL` 是否设置正确
- 浏览器开发者工具中 `/api` 请求是否命中正确后端地址

## 7.3 AI 服务无法转发事件

排查要点：

- `http://localhost:8080/api/ai/ingest` 是否可访问
- AI 服务环境变量 `SPRING_BOOT_URL` 是否配置正确
- 8000/8080 端口是否冲突

## 7.4 Docker 启动失败

排查要点：

- Docker Desktop 是否已启动
- `.env` 变量是否完整
- 3306/8080/8081 端口是否与本机服务冲突
- 使用 `docker compose logs -f` 查看容器日志

## 8. 5 分钟答辩前快速自检

答辩开始前建议按以下顺序快速检查：

1. 执行 `docker compose ps`（或分别查看本地服务进程）确认服务在线
2. 打开前端首页（`5173` 或 `8081`）确认页面可访问
3. 访问 `http://localhost:8000/health` 确认 AI 服务存活
4. 检查后端日志中是否存在数据库连接异常
5. 准备一条演示链路：页面展示 -> AI 事件 -> 后端接收记录

完成以上检查后，即可进入作品功能演示阶段。
