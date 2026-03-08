# AI老人跌倒检测系统项目文档

## 1. 项目简介

本项目实现一个 **基于摄像头的老人跌倒检测系统（Fall Detection
System）**。
系统通过计算机视觉和深度学习技术，对视频中的人体进行检测与行为分析，当检测到疑似跌倒行为时触发报警。

项目采用 **分阶段实现策略**：

1.  第一阶段：基于 Bounding Box 的简单跌倒检测
2.  第二阶段：加入人体姿态识别（Pose Estimation）提高准确率
3.  第三阶段：多特征融合判断，减少误报

------------------------------------------------------------------------

# 2. 系统整体架构

系统整体流程如下：

Camera Video → Person Detection (YOLO) → Object Tracking (ByteTrack /
DeepSORT) → Feature Extraction → Fall Detection Logic → Alarm /
Notification

第二阶段系统架构：

Camera → YOLO Person Detection → Pose Estimation → Feature Extraction →
Fall Detection Model → Alarm System

------------------------------------------------------------------------

# 3. 技术选型

  模块       技术
  ---------- ----------------------------------
  视频输入   OpenCV
  人体检测   YOLOv8
  人体跟踪   ByteTrack / DeepSORT
  姿态识别   OpenPose / MediaPipe / BlazePose
  数据处理   Python
  深度学习   PyTorch
  报警系统   HTTP / MQTT / 本地报警

------------------------------------------------------------------------

# 4. 第一阶段：Bounding Box 跌倒检测

## 4.1 原理

通过人体检测获得 **Bounding
Box**，分析人体框的形状变化和运动速度判断是否跌倒。

人体框：

(x, y, width, height)

------------------------------------------------------------------------

## 4.2 关键特征

### 1）宽高比

ratio = height / width

  状态   ratio
  ------ ----------
  站立   2 \~ 4
  坐下   1.5 \~ 2
  跌倒   0.5 \~ 1

### 2）人体高度变化

height_change = height(t) - height(t-1)

跌倒通常伴随 **高度快速下降**。

### 3）人体中心点下降速度

center_y = y + height/2

velocity = center_y(t) - center_y(t-1)

跌倒时速度明显增加。

简单判断逻辑：

if ratio \< 1.2 and center_drop_speed \> threshold: fall = True

------------------------------------------------------------------------

# 5. 第二阶段：人体姿态识别

Bounding Box 方法存在误判问题，例如：

-   坐下
-   躺下
-   弯腰

因此需要加入 **人体关键点检测（Pose Estimation）**。

## 5.1 人体关键点

Head Shoulder Elbow Wrist Hip Knee Ankle

## 5.2 姿态特征

### 1）身体倾斜角

Shoulder → Hip

body_angle = angle(shoulder, hip)

  状态   角度
  ------ ----------
  站立   ≈90°
  弯腰   45°\~80°
  跌倒   0°\~30°

### 2）头部高度变化

head_drop = head_y(t) - head_y(t-1)

跌倒时变化明显。

### 3）膝盖弯曲程度

通过 hip-knee-ankle 三点计算膝关节角度。

------------------------------------------------------------------------

# 6. 第三阶段：融合检测

最终系统使用 **多特征融合**：

Bounding Box Ratio + Body Angle + Center Drop Speed

示例判断：

if ratio \< 1.2 and body_angle \< 40 and center_drop_speed \> threshold:
fall = True

------------------------------------------------------------------------

# 7. 系统模块设计

## 7.1 视频采集模块

负责从摄像头读取视频流。

功能：

-   摄像头初始化
-   帧读取
-   视频预处理

工具：OpenCV

## 7.2 人体检测模块

模型：YOLOv8

输出：

\[x, y, width, height\]

## 7.3 目标跟踪模块

算法：

-   ByteTrack
-   DeepSORT

输出：

person_id bounding_box

## 7.4 姿态识别模块

模型：

-   MediaPipe Pose
-   OpenPose
-   BlazePose

输出关键点坐标。

## 7.5 跌倒检测模块

输入：

-   Bounding Box
-   Pose Keypoints
-   历史轨迹

输出：

Fall / Normal

## 7.6 报警模块

当检测到跌倒时可执行：

-   本地报警
-   发送通知
-   推送服务器

------------------------------------------------------------------------

# 8. 项目目录结构

fall-detection/

data/ models/ yolo/ pose/

src/ camera.py detector.py tracker.py pose.py fall_detector.py alarm.py

config/ config.yaml

utils/

main.py README.md

------------------------------------------------------------------------

# 9. 未来优化方向

## 9.1 时间序列模型

-   LSTM
-   GRU
-   Temporal CNN

用于分析连续动作。

## 9.2 数据集训练

可使用：

-   UR Fall Detection Dataset
-   Le2i Fall Dataset
-   SisFall Dataset

## 9.3 夜间检测

可加入：

-   红外摄像头
-   低光增强算法

------------------------------------------------------------------------

# 10. 项目总结

实现路线：

Bounding Box 检测 → 速度特征 → Pose Estimation → 多特征融合

该方案具有：

-   实现难度适中
-   可逐步迭代
-   可扩展性强

适用于智慧养老、医院监护、居家监控。
