# SmartCare 前端开发文档

## Web 管理平台（Vue3 + TypeScript + Element Plus）

---

# 1. 项目简介

SmartCare 前端管理平台用于展示社区养老监护系统的数据，并提供基础管理能力。

主要功能：

* 查看告警记录
* 查看老人信息
* 系统数据统计
* 实时状态展示

前端通过 HTTP API 与后端 Spring Boot 服务通信。

系统架构：

```
AI识别模块
    ↓
Spring Boot 后端
    ↓
REST API
    ↓
Web 管理平台
```

---

# 2. 技术栈

| 类型      | 技术           |
| ------- | ------------ |
| 前端框架    | Vue 3        |
| 编程语言    | TypeScript   |
| 构建工具    | Vite         |
| UI组件库   | Element Plus |
| HTTP客户端 | Axios        |
| 路由      | Vue Router   |
| 状态管理    | Pinia        |
| 图表库     | ECharts      |

选择理由：

* Vue3 生态成熟
* TypeScript 提高代码可靠性
* Element Plus 提供完整后台 UI
* Axios 易于 API 调用
* Vite 构建速度快

---

# 3. 项目运行

安装依赖：记得所有命令都在smart/frontend的终端运行！！！

```
npm install
```

启动开发服务器：

```
npm run dev
```

构建生产版本：

```
npm run build
```

---

# 4. 项目目录结构

目录结构：

```
src
 ├─ api
 │   ├─ alerts.ts
 │   ├─ elders.ts
 │
 ├─ assets
 │
 ├─ components
 │   ├─ AlertTable.vue
 │   └─ StatCard.vue
 │
 ├─ composables
 │   ├─ useAlerts.ts
 │   └─ useElders.ts
 │
 ├─ layouts
 │   └─ MainLayout.vue
 │
 ├─ router
 │   └─ index.ts
 │
 ├─ stores
 │   └─ alertStore.ts
 │
 ├─ types
 │   ├─ alert.ts
 │   ├─ elder.ts
 │   ├─ event.ts
 │   └─ api.ts
 │
 ├─ utils
 │   └─ request.ts
 │
 ├─ views
 │   ├─ Dashboard.vue
 │   ├─ Alerts.vue
 │   └─ Elders.vue
 │
 ├─ App.vue
 └─ main.ts
```

目录说明：

| 目录     | 作用           |
| ------ | ------------ |
| api    | API请求封装      |
| views  | 页面组件         |
| layout | 页面布局         |
| router | 路由管理         |
| store  | 全局状态         |
| types  | TypeScript类型 |

---

# 6. 后台布局设计

后台管理系统布局：

```
+-----------------------------+
|         Header              |
+------+----------------------+
| Menu |        Main          |
|      |                      |
|      |                      |
+------+----------------------+
```

使用组件：

```
el-container
el-aside
el-header
el-main
```

---

# 7. 开发流程(不必完全一致)

推荐开发顺序：

### 第一步

完成项目初始化：

```
Vue3 + TypeScript + Vite
```

---

### 第二步

实现后台布局：

```
MainLayout
侧边菜单
```

---

### 第三步

开发 Alerts 页面

目标：

```
GET /api/alerts
```

成功显示数据。

---

### 第四步

开发 Elders 页面

目标：

```
GET /api/elders
```

---

### 第五步

开发 Dashboard

内容：

* 系统统计
* 图表展示

---

# 8. 实时告警（扩展功能）

系统可以加入实时通知：

```
AI模块
↓
Spring Boot
↓
WebSocket
↓
前端
```

当检测到跌倒事件时：

前端弹出提示：

```
⚠ 跌倒告警

老人：张三
时间：10:20
```

可以使用 Element Plus 的 Notification 组件实现。

---

# 9. MVP 页面结构

```
SmartCare 管理系统

Dashboard
告警记录
老人管理
```

系统目标：

* 实现基础管理功能
* 展示 AI 识别事件
* 支持系统演示

---

# 10. 后续扩展

未来可以增加：

* 实时视频监控
* AI事件统计
* 社区数据看板
* 多摄像头管理
* 用户权限管理

---


# 11. 总结

SmartCare 前端系统提供了：

* 清晰的模块结构
* TypeScript 类型支持
* Element Plus UI组件
* 与 Spring Boot API 的对接能力

该架构适用于：

* 毕业设计
* AI监护系统
* 管理后台开发
* 企业级前端项目
