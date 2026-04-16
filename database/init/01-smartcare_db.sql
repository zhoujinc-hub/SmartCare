-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS smartcare_db
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. 使用数据库
USE smartcare_db;

-- 3. 创建应用用户（匹配你后端的配置）
CREATE USER IF NOT EXISTS 'smartcare_admin'@'%' IDENTIFIED BY 'password';

-- 4. 授权（允许远程连接）
GRANT ALL PRIVILEGES ON smartcare_db.* TO 'smartcare_admin'@'%';

-- 5. 刷新权限
FLUSH PRIVILEGES;

-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: smartcare_db
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alert_logs`
--

DROP TABLE IF EXISTS `alert_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alert_logs` (
  `alert_id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `recipient_id` bigint NOT NULL COMMENT '接收人ID(关联users)',
  `recipient_type` tinyint NOT NULL COMMENT '1:家属 2:管理员',
  `recipient_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收手机号',
  `send_method` tinyint NOT NULL COMMENT '1:短信 2:APP推送 3:电话',
  `send_status` tinyint DEFAULT '1' COMMENT '0:失败 1:成功 2:发送中',
  `error_msg` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sent_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`alert_id`),
  KEY `event_id` (`event_id`),
  KEY `recipient_id` (`recipient_id`),
  CONSTRAINT `alert_logs_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `fall_events` (`event_id`) ON DELETE CASCADE,
  CONSTRAINT `alert_logs_ibfk_2` FOREIGN KEY (`recipient_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alert_logs`
--

LOCK TABLES `alert_logs` WRITE;
/*!40000 ALTER TABLE `alert_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `alert_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cameras`
--

DROP TABLE IF EXISTS `cameras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cameras` (
  `camera_id` bigint NOT NULL AUTO_INCREMENT,
  `camera_type` tinyint NOT NULL COMMENT '0:家庭 1:社区',
  `camera_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_serial` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `stream_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location_desc` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `status` tinyint DEFAULT '1' COMMENT '0:离线 1:在线',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`camera_id`),
  UNIQUE KEY `device_serial` (`device_serial`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cameras`
--

LOCK TABLES `cameras` WRITE;
/*!40000 ALTER TABLE `cameras` DISABLE KEYS */;
/*!40000 ALTER TABLE `cameras` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `elders`
--

DROP TABLE IF EXISTS `elders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `elders` (
  `elder_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `age` int DEFAULT NULL,
  `gender` tinyint DEFAULT NULL COMMENT '0:女 1:男',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `health_notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`elder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `elders`
--

LOCK TABLES `elders` WRITE;
/*!40000 ALTER TABLE `elders` DISABLE KEYS */;
/*!40000 ALTER TABLE `elders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fall_events`
--

DROP TABLE IF EXISTS `fall_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fall_events` (
  `event_id` bigint NOT NULL AUTO_INCREMENT,
  `camera_id` bigint NOT NULL,
  `elder_id` bigint DEFAULT NULL COMMENT 'NULL表示未注册路人',
  `elder_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路人时记录姓名',
  `is_registered` tinyint NOT NULL DEFAULT '0' COMMENT '0:未注册 1:已注册',
  `fall_time` timestamp NOT NULL COMMENT '摔倒发生时间',
  `detect_time` timestamp NOT NULL COMMENT '系统检测时间',
  `video_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `screenshot_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `confidence` decimal(3,2) DEFAULT NULL COMMENT 'AI置信度 0-1',
  `status` tinyint DEFAULT '1' COMMENT '1:待处理 2:已处理 3:误报',
  `processed_by` bigint DEFAULT NULL,
  `processed_at` timestamp NULL DEFAULT NULL,
  `process_notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`event_id`),
  KEY `camera_id` (`camera_id`),
  KEY `elder_id` (`elder_id`),
  KEY `processed_by` (`processed_by`),
  CONSTRAINT `fall_events_ibfk_1` FOREIGN KEY (`camera_id`) REFERENCES `cameras` (`camera_id`),
  CONSTRAINT `fall_events_ibfk_2` FOREIGN KEY (`elder_id`) REFERENCES `elders` (`elder_id`),
  CONSTRAINT `fall_events_ibfk_3` FOREIGN KEY (`processed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fall_events`
--

LOCK TABLES `fall_events` WRITE;
/*!40000 ALTER TABLE `fall_events` DISABLE KEYS */;
/*!40000 ALTER TABLE `fall_events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relations`
--

DROP TABLE IF EXISTS `relations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `relations` (
  `relation_id` bigint NOT NULL AUTO_INCREMENT,
  `elder_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `relationship` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '父子/母女等',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `unique_relation` (`elder_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `relations_ibfk_1` FOREIGN KEY (`elder_id`) REFERENCES `elders` (`elder_id`) ON DELETE CASCADE,
  CONSTRAINT `relations_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relations`
--

LOCK TABLES `relations` WRITE;
/*!40000 ALTER TABLE `relations` DISABLE KEYS */;
/*!40000 ALTER TABLE `relations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_type` tinyint NOT NULL COMMENT '1:管理员 2:家属',
  `status` tinyint DEFAULT '1' COMMENT '0:禁用 1:启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-21 15:24:39
