# 系统架构

## 概述
SmartCare 采用模块化架构，分离 AI 处理、后端服务、数据库和前端管理平台。
后端事件服务已从 Express 迁移为 Spring Boot。

## 组件
1. AI 识别服务（Python）
2. 事件服务 API（Java / Spring Boot）
3. 数据库（MySQL）
4. Web 管理平台（前端）

## 架构流程
摄像头 -> AI 识别 -> 事件 API（Spring Boot） -> 数据库 -> Web 仪表盘

## 职责

### AI 识别服务
- 视频处理
- 姿态检测
- 跌倒检测
- 事件生成

### 事件 API（Spring Boot）
- 接收 AI 事件
- 校验数据
- 存储告警
- 提供 REST API

### 数据库
存储：
- 老人信息
- 告警记录
- 行为事件

### Web 平台
展示：
- 老人列表
- 告警历史
- 系统状态
