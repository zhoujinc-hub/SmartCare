-- Safe MySQL merge script generated from uploaded MySQL schema + SQLite database

-- Usage: mysql -h 114.215.169.226 -P 3306 -u <user> -p smartcare_db < merge_sqlite_into_mysql_safe.sql

-- This script does NOT DROP existing MySQL tables.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS=0;

-- 1) Existing SmartCare MySQL tables, converted to CREATE TABLE IF NOT EXISTS

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

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `alert_logs` (
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


--
-- Table structure for table `cameras`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `cameras` (
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


--
-- Table structure for table `elders`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `elders` (
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


--
-- Table structure for table `fall_events`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `fall_events` (
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


--
-- Table structure for table `relations`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `relations` (
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


--
-- Table structure for table `users`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `users` (
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

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-21 15:24:39

-- 2) SQLite/Django tables converted into MySQL tables and data


-- Table converted from SQLite: auth_group
CREATE TABLE IF NOT EXISTS `auth_group` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(150) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table converted from SQLite: auth_group_permissions
CREATE TABLE IF NOT EXISTS `auth_group_permissions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table converted from SQLite: auth_permission
CREATE TABLE IF NOT EXISTS `auth_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `content_type_id` BIGINT NOT NULL,
  `codename` VARCHAR(100) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `auth_permission` (`id`, `content_type_id`, `codename`, `name`) VALUES
(1, 1, 'add_logentry', 'Can add log entry'),
(2, 1, 'change_logentry', 'Can change log entry'),
(3, 1, 'delete_logentry', 'Can delete log entry'),
(4, 1, 'view_logentry', 'Can view log entry'),
(5, 2, 'add_permission', 'Can add permission'),
(6, 2, 'change_permission', 'Can change permission'),
(7, 2, 'delete_permission', 'Can delete permission'),
(8, 2, 'view_permission', 'Can view permission'),
(9, 3, 'add_group', 'Can add group'),
(10, 3, 'change_group', 'Can change group'),
(11, 3, 'delete_group', 'Can delete group'),
(12, 3, 'view_group', 'Can view group'),
(13, 4, 'add_user', 'Can add user'),
(14, 4, 'change_user', 'Can change user'),
(15, 4, 'delete_user', 'Can delete user'),
(16, 4, 'view_user', 'Can view user'),
(17, 5, 'add_contenttype', 'Can add content type'),
(18, 5, 'change_contenttype', 'Can change content type'),
(19, 5, 'delete_contenttype', 'Can delete content type'),
(20, 5, 'view_contenttype', 'Can view content type'),
(21, 6, 'add_session', 'Can add session'),
(22, 6, 'change_session', 'Can change session'),
(23, 6, 'delete_session', 'Can delete session'),
(24, 6, 'view_session', 'Can view session'),
(25, 7, 'add_welcome', 'Can add welcome'),
(26, 7, 'change_welcome', 'Can change welcome'),
(27, 7, 'delete_welcome', 'Can delete welcome'),
(28, 7, 'view_welcome', 'Can view welcome'),
(29, 8, 'add_banner', 'Can add banner'),
(30, 8, 'change_banner', 'Can change banner'),
(31, 8, 'delete_banner', 'Can delete banner'),
(32, 8, 'view_banner', 'Can view banner'),
(33, 9, 'add_notice', 'Can add notice'),
(34, 9, 'change_notice', 'Can change notice'),
(35, 9, 'delete_notice', 'Can delete notice'),
(36, 9, 'view_notice', 'Can view notice'),
(37, 10, 'add_area', 'Can add area'),
(38, 10, 'change_area', 'Can change area'),
(39, 10, 'delete_area', 'Can delete area'),
(40, 10, 'view_area', 'Can view area'),
(41, 11, 'add_collection', 'Can add collection'),
(42, 11, 'change_collection', 'Can change collection'),
(43, 11, 'delete_collection', 'Can delete collection'),
(44, 11, 'view_collection', 'Can view collection'),
(45, 12, 'add_userinfo', 'Can add user info'),
(46, 12, 'change_userinfo', 'Can change user info'),
(47, 12, 'delete_userinfo', 'Can delete user info'),
(48, 12, 'view_userinfo', 'Can view user info'),
(49, 13, 'add_activity', 'Can add activity'),
(50, 13, 'change_activity', 'Can change activity'),
(51, 13, 'delete_activity', 'Can delete activity'),
(52, 13, 'view_activity', 'Can view activity'),
(53, 14, 'add_joinrecord', 'Can add join record'),
(54, 14, 'change_joinrecord', 'Can change join record'),
(55, 14, 'delete_joinrecord', 'Can delete join record'),
(56, 14, 'view_joinrecord', 'Can view join record');

-- Table converted from SQLite: auth_user
CREATE TABLE IF NOT EXISTS `auth_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `password` VARCHAR(128) NOT NULL,
  `last_login` DATETIME NULL,
  `is_superuser` TINYINT(1) NOT NULL,
  `username` VARCHAR(150) NOT NULL,
  `last_name` VARCHAR(150) NOT NULL,
  `email` VARCHAR(254) NOT NULL,
  `is_staff` TINYINT(1) NOT NULL,
  `is_active` TINYINT(1) NOT NULL,
  `date_joined` DATETIME NOT NULL,
  `first_name` VARCHAR(150) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `auth_user` (`id`, `password`, `last_login`, `is_superuser`, `username`, `last_name`, `email`, `is_staff`, `is_active`, `date_joined`, `first_name`) VALUES
(1, 'pbkdf2_sha256$1000000$SKmibmoNhtTuvXCK5xBFPq$bp696FSKEJ35oU7BZ2bf7aVeqnWSZ5Cs/2i/1fj6ps4=', '2026-01-05 13:10:12.388240', 1, 'cyxhnb', '', '1509117274@qq.com', 1, 1, '2025-11-18 00:56:09.956287', '');

-- Table converted from SQLite: auth_user_groups
CREATE TABLE IF NOT EXISTS `auth_user_groups` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `group_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table converted from SQLite: auth_user_user_permissions
CREATE TABLE IF NOT EXISTS `auth_user_user_permissions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table converted from SQLite: django_admin_log
CREATE TABLE IF NOT EXISTS `django_admin_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `object_id` TEXT NULL,
  `object_repr` VARCHAR(200) NOT NULL,
  `action_flag` BIGINT NOT NULL,
  `change_message` TEXT NOT NULL,
  `content_type_id` BIGINT NULL,
  `user_id` BIGINT NOT NULL,
  `action_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `django_admin_log` (`id`, `object_id`, `object_repr`, `action_flag`, `change_message`, `content_type_id`, `user_id`, `action_time`) VALUES
(1, '1', 'Welcome object (1)', 1, '[{"added": {}}]', 7, 1, '2025-11-18 01:55:06.935491'),
(2, '2', 'Welcome object (2)', 1, '[{"added": {}}]', 7, 1, '2025-11-18 01:59:08.582360'),
(3, '2', 'welcome/splash3.png', 3, '', 7, 1, '2025-11-18 10:12:24.723344'),
(4, '1', 'welcome/splash2.png', 3, '', 7, 1, '2025-11-18 10:12:24.723344'),
(5, '3', 'welcome/splash2_QAdfakb.png', 1, '[{"added": {}}]', 7, 1, '2025-11-18 10:12:45.765847'),
(6, '4', 'welcome/splash3_3gTH85m.png', 1, '[{"added": {}}]', 7, 1, '2025-11-18 10:12:55.134291'),
(7, '4', 'welcome/splash3_3gTH85m.png', 3, '', 7, 1, '2025-11-18 10:14:38.315243'),
(8, '3', 'welcome/splash2_QAdfakb.png', 3, '', 7, 1, '2025-11-18 10:14:38.315243'),
(9, '5', 'welcome/splash2_ELd1yXB.png', 1, '[{"added": {}}]', 7, 1, '2025-11-18 10:14:53.640919'),
(10, '6', 'welcome/splash3_rlCFt5Y.png', 1, '[{"added": {}}]', 7, 1, '2025-11-18 10:15:01.686512'),
(11, '1', 'banner/banner1.png', 1, '[{"added": {}}]', 8, 1, '2025-11-19 08:48:39.748083'),
(12, '2', 'banner/banner2.png', 1, '[{"added": {}}]', 8, 1, '2025-11-19 08:48:46.169697'),
(13, '3', 'banner/banner3.png', 1, '[{"added": {}}]', 8, 1, '2025-11-19 08:48:52.471772'),
(14, '1', '大型交友会在本社区举行,欢迎大家莅临~~', 1, '[{"added": {}}]', 9, 1, '2025-11-19 09:26:19.640034'),
(15, '1', 'banner/banner1.png', 2, '[{"changed": {"fields": ["\\u662f\\u5426\\u5220\\u9664"]}}]', 8, 1, '2025-11-19 10:36:59.542391'),
(16, '1', '注意:大型交友会在本社区举行,欢迎大家莅临~~', 2, '[{"changed": {"fields": ["\\u516c\\u544a\\u6807\\u9898", "\\u516c\\u544a\\u5185\\u5bb9"]}}]', 9, 1, '2025-11-19 10:43:04.147738'),
(17, '1', 'justin', 1, '[{"added": {}}]', 12, 1, '2025-11-19 12:06:05.492462'),
(18, '1', '1单元1号楼', 1, '[{"added": {}}]', 10, 1, '2025-11-19 12:06:49.433810'),
(19, '2', '1单元2号楼', 1, '[{"added": {}}]', 10, 1, '2025-11-19 12:07:17.783369'),
(20, '3', '1单元3号楼', 1, '[{"added": {}}]', 10, 1, '2025-11-19 12:07:32.201921'),
(21, '3', '1单元3号楼', 3, '', 10, 1, '2025-11-19 12:12:03.276616'),
(22, '4', '2单元3号楼', 1, '[{"added": {}}]', 10, 1, '2025-11-19 12:12:24.119020'),
(23, '1', '刘亦菲', 1, '[{"added": {}}]', 11, 1, '2025-12-04 09:51:23.076334'),
(24, '2', '迪丽热巴', 1, '[{"added": {}}]', 11, 1, '2025-12-04 09:58:41.111175'),
(25, '2', '迪丽热巴', 2, '[]', 11, 1, '2025-12-09 20:41:29.939597'),
(26, '1', '刘亦菲', 2, '[]', 11, 1, '2025-12-09 20:41:32.561928'),
(27, '2', '迪丽热巴', 2, '[]', 11, 1, '2025-12-09 20:48:01.708800'),
(28, '1', '刘亦菲', 2, '[]', 11, 1, '2025-12-09 20:48:03.533689'),
(29, '2', '迪丽热巴', 2, '[{"changed": {"fields": ["\\u5934\\u50cf"]}}]', 11, 1, '2025-12-09 20:53:15.126369'),
(30, '3', '迪丽热巴', 1, '[{"added": {}}]', 11, 1, '2025-12-09 20:58:21.144532'),
(31, '3', '迪丽热巴', 2, '[{"changed": {"fields": ["\\u5934\\u50cf"]}}]', 11, 1, '2025-12-09 20:58:39.501921'),
(32, '3', '迪丽热巴', 2, '[]', 11, 1, '2025-12-12 09:14:32.272498'),
(33, '1', '刘亦菲', 2, '[]', 11, 1, '2025-12-12 09:14:34.237610'),
(34, '1', '刘亦菲', 2, '[]', 11, 1, '2025-12-13 21:03:58.191381'),
(35, '3', '迪丽热巴', 2, '[]', 11, 1, '2025-12-13 21:04:01.822767'),
(36, '3', '迪丽热巴', 2, '[]', 11, 1, '2025-12-14 19:45:20.747438'),
(37, '1', '刘亦菲', 2, '[]', 11, 1, '2025-12-14 19:45:23.127961'),
(38, '4', '2单元3号楼', 2, '[]', 10, 1, '2025-12-14 19:45:27.271419'),
(39, '2', '1单元2号楼', 2, '[]', 10, 1, '2025-12-14 19:45:28.860473'),
(40, '1', '1单元1号楼', 2, '[]', 10, 1, '2025-12-14 19:45:30.241334'),
(41, '3', '迪丽热巴', 2, '[]', 11, 1, '2025-12-16 20:59:40.703736'),
(42, '1', '刘亦菲', 2, '[]', 11, 1, '2025-12-16 20:59:42.506677'),
(43, '4', '2单元3号楼', 2, '[]', 10, 1, '2025-12-16 20:59:50.432164'),
(44, '2', '1单元2号楼', 2, '[]', 10, 1, '2025-12-16 20:59:53.698838'),
(45, '1', '1单元1号楼', 2, '[]', 10, 1, '2025-12-16 20:59:55.223654'),
(46, '3', '迪丽热巴', 2, '[]', 11, 1, '2025-12-18 20:03:33.435508'),
(47, '1', '刘亦菲', 2, '[]', 11, 1, '2025-12-18 20:03:35.483097'),
(48, '4', '2单元3号楼', 2, '[]', 10, 1, '2025-12-18 20:03:41.065487'),
(49, '2', '1单元2号楼', 2, '[]', 10, 1, '2025-12-18 20:03:42.581945'),
(50, '1', '1单元1号楼', 2, '[]', 10, 1, '2025-12-18 20:03:43.978089'),
(51, '2', '小区中心举行活动', 1, '[{"added": {}}]', 9, 1, '2026-01-02 13:55:08.960428'),
(52, '2', '小区中心举行活动', 2, '[]', 9, 1, '2026-01-02 13:55:11.422203'),
(53, '3', '大型交友会', 1, '[{"added": {}}]', 9, 1, '2026-01-02 13:55:40.481964'),
(54, '1', '注意:大型交友会在本社区举行,欢迎大家莅临~~', 2, '[{"changed": {"fields": ["\\u516c\\u544a\\u5185\\u5bb9", "\\u516c\\u544a\\u56fe\\u7247"]}}]', 9, 1, '2026-01-02 13:59:48.352326'),
(55, '2', '小区中心举行活动', 2, '[{"changed": {"fields": ["\\u516c\\u544a\\u5185\\u5bb9"]}}]', 9, 1, '2026-01-02 13:59:59.257551'),
(56, '3', '大型交友会', 2, '[{"changed": {"fields": ["\\u516c\\u544a\\u5185\\u5bb9"]}}]', 9, 1, '2026-01-02 14:00:12.730317'),
(57, '1', '注意:大型交友会在本社区举行,欢迎大家莅临~~', 2, '[]', 9, 1, '2026-01-02 14:10:47.053816'),
(58, '1', 'justin', 2, '[]', 12, 1, '2026-01-02 14:18:26.982021'),
(59, '3', '大型交友会', 2, '[]', 9, 1, '2026-01-02 14:18:37.011031'),
(60, '2', '小区中心举行活动', 2, '[{"changed": {"fields": ["\\u516c\\u544a\\u56fe\\u7247"]}}]', 9, 1, '2026-01-02 14:19:53.982636'),
(61, '3', '大型交友会', 2, '[{"changed": {"fields": ["\\u516c\\u544a\\u56fe\\u7247"]}}]', 9, 1, '2026-01-02 14:20:03.864079'),
(62, '1', '秋季运动会', 1, '[{"added": {}}]', 13, 1, '2026-01-02 16:18:47.614109'),
(63, '2', '跳远比赛', 1, '[{"added": {}}]', 13, 1, '2026-01-02 16:19:46.212872'),
(64, '3', '吃西瓜比赛', 1, '[{"added": {}}]', 13, 1, '2026-01-02 16:20:28.743803'),
(65, '2', '跳远比赛', 2, '[]', 13, 1, '2026-01-02 16:23:12.192008'),
(66, '2', '跳远比赛', 2, '[]', 13, 1, '2026-01-02 16:23:19.887965'),
(67, '1', '秋季运动会', 2, '[{"changed": {"fields": ["\\u62a5\\u540d\\u4eba\\u6570", "\\u603b\\u4eba\\u6570"]}}]', 13, 1, '2026-01-02 16:23:26.957987'),
(68, '3', '吃西瓜比赛', 2, '[{"changed": {"fields": ["\\u62a5\\u540d\\u4eba\\u6570", "\\u603b\\u4eba\\u6570"]}}]', 13, 1, '2026-01-02 16:23:33.928800');

-- Table converted from SQLite: django_content_type
CREATE TABLE IF NOT EXISTS `django_content_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `app_label` VARCHAR(100) NOT NULL,
  `model` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `django_content_type` (`id`, `app_label`, `model`) VALUES
(1, 'admin', 'logentry'),
(2, 'auth', 'permission'),
(3, 'auth', 'group'),
(4, 'auth', 'user'),
(5, 'contenttypes', 'contenttype'),
(6, 'sessions', 'session'),
(7, 'myapp', 'welcome'),
(8, 'myapp', 'banner'),
(9, 'myapp', 'notice'),
(10, 'myapp', 'area'),
(11, 'myapp', 'collection'),
(12, 'myapp', 'userinfo'),
(13, 'myapp', 'activity'),
(14, 'myapp', 'joinrecord');

-- Table converted from SQLite: django_migrations
CREATE TABLE IF NOT EXISTS `django_migrations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `app` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `applied` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES
(1, 'contenttypes', '0001_initial', '2025-11-18 00:16:41.559526'),
(2, 'auth', '0001_initial', '2025-11-18 00:16:41.571074'),
(3, 'admin', '0001_initial', '2025-11-18 00:16:41.586550'),
(4, 'admin', '0002_logentry_remove_auto_add', '2025-11-18 00:16:41.594493'),
(5, 'admin', '0003_logentry_add_action_flag_choices', '2025-11-18 00:16:41.598498'),
(6, 'contenttypes', '0002_remove_content_type_name', '2025-11-18 00:16:41.610135'),
(7, 'auth', '0002_alter_permission_name_max_length', '2025-11-18 00:16:41.616368'),
(8, 'auth', '0003_alter_user_email_max_length', '2025-11-18 00:16:41.626303'),
(9, 'auth', '0004_alter_user_username_opts', '2025-11-18 00:16:41.634512'),
(10, 'auth', '0005_alter_user_last_login_null', '2025-11-18 00:16:41.643383'),
(11, 'auth', '0006_require_contenttypes_0002', '2025-11-18 00:16:41.645472'),
(12, 'auth', '0007_alter_validators_add_error_messages', '2025-11-18 00:16:41.651058'),
(13, 'auth', '0008_alter_user_username_max_length', '2025-11-18 00:16:41.658845'),
(14, 'auth', '0009_alter_user_last_name_max_length', '2025-11-18 00:16:41.666780'),
(15, 'auth', '0010_alter_group_name_max_length', '2025-11-18 00:16:41.674387'),
(16, 'auth', '0011_update_proxy_permissions', '2025-11-18 00:16:41.680825'),
(17, 'auth', '0012_alter_user_first_name_max_length', '2025-11-18 00:16:41.688810'),
(18, 'myapp', '0001_initial', '2025-11-18 00:16:41.693175'),
(19, 'sessions', '0001_initial', '2025-11-18 00:16:41.699338'),
(20, 'myapp', '0002_banner_notice_alter_welcome_options', '2025-11-18 09:28:41.746261'),
(21, 'myapp', '0003_area_userinfo_collection_area_user', '2025-11-19 12:04:17.765039'),
(22, 'myapp', '0004_collection_face_token', '2025-12-19 15:20:55.962815'),
(23, 'myapp', '0005_rename_igm_notice_img', '2026-01-02 13:48:01.749049'),
(24, 'myapp', '0006_activity_userinfo_mobile_joinrecord_and_more', '2026-01-02 16:14:42.608986');

-- Table converted from SQLite: django_session
CREATE TABLE IF NOT EXISTS `django_session` (
  `session_key` VARCHAR(40) NOT NULL,
  `session_data` TEXT NOT NULL,
  `expire_date` DATETIME NOT NULL,
  PRIMARY KEY (`session_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `django_session` (`session_key`, `session_data`, `expire_date`) VALUES
('sf8skjtdxy6ov8w8k0b8t795taaunhdc', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vLZQc:0hLOLa4pgwLNaXKsX9kdBNaM8cjudsteNyBJqTSA1Dw', '2025-12-03 12:04:22.879312'),
('qlfckwknvq6332xz7d6driwuz9obkgkj', '.eJzFVMFuozAU_JXI5ySAsQ302HuPe1qq6PnZJOyCiSCoqqr8e_1CKjWUNEl3pZxG8oyHN_Ms3tgK-t1m1Xe2XZWGPbCIzT-facC_1hFh_oBbN0ts3K4t9ZIkyyPbLZ8aY6vHo_bEYAPdxt-WymiudYKhR5NwabQClIZ7iOIMOQiboSgwC-NCWh5KmakwskakuhA6JNPaur7zXr_fcuagtjl7mOUsz_tUgzgARh6kSNGDitOUIBFxzuZeWPrRhysFdLMCFt2mtJVZQLUbBDWF6Ejy5QuJ5AczHidfzVoyo7AD1bfVwARg6tIF1EVAdDDwYMyvsxJPHmW6tWCw7Ws9PdJ_CL2fz34adP9MpC0NcVEY8ikvi-LMFGTSLbBZf1PZum367YXOBs3dS7sq6LiyeHwQTVhnWaYI0kTSiJpzGr9Ae2Y7WLZY2YvvWUYKDwUIIL9UpVf4jbdUv8J2G7hmV6I9v6cT1fWb-mnwiQ5vDjtejJww_TzRPzb4Yits6osVfsju0uHNcccdqgnTtFDkpgQY2o8qrnnV0x1qcO67P-yJ6i4N3hx23GAyPhD7Z7Z_B3IGau0:1vLBFH:MhVy6hRSOXW-jUB6U4AAGqC0e8vHGOamWhDjrVFjDz4', '2025-12-02 10:15:03.462417'),
('o1euobzciepujxxwe4ox58gnlpi16f6p', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vNjps:x522fhKPb4BC0J2xsFm0OrjUZjMNiOOHDbcvzsao4GM', '2025-12-09 11:35:24.220774'),
('5e82ko6od1irs9vzhxntrwxqjzj440fv', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vQxdF:cOvBXObPNOBAp1sUs3Gp8Hgh0s0PliprqNuys2mkZ5s', '2025-12-18 08:55:41.243811'),
('oxvur8zcsw564sp8c9kyrtyopy7wdbpk', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vR81w:skrEyn66kdrmmfYEZrciileAqJ1S_nUe6YLPnHYUPXo', '2025-12-18 20:01:52.093204'),
('eze4ubl9sdb275k7q06k0d6swngh5erp', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vR86r:dN0Me8uCEv5WLZxBYEVqlvBSFF1ayKIknVX1VzKN0y0', '2025-12-18 20:06:57.608565'),
('mk5ebm73h8tfcrqtidr7cvf9z3lv59wr', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vRQWn:gaya54WefuK93IvSJsQjXthacegYzpUHn5PjAQwtQ2w', '2025-12-19 15:46:57.437198'),
('syui9wubztz4fsmmfi8ici3y5pwuv7fu', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vSxDR:23Mlp5G-HbbMb25tuIKGOfz36yeTDOyToYxpCpg-Dgc', '2025-12-23 20:53:17.205146'),
('erle1gjmp0eirtmg6c732id5gzxitckc', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vTrjg:13JU_5Gv0r7OkQgwZxWUyN-x4kZVY_YZeRovoZi-qaM', '2025-12-26 09:14:20.107520'),
('4sd5y8415f8sw5m6pnt77p6p7lk9huys', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vWCiJ:11VgO7oThRLGHBn2of4YWjll2AKk3jkkBalG2TIVyKg', '2026-01-01 20:02:35.800371'),
('tgl3u1yoxdnlztuykmzdp8z5522tls94', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vWTeW:XwRqKhgWqa1lKThrincUOBbU9ncxmb6zll8b1iGb2SQ', '2026-01-02 14:07:48.659234'),
('v4msz6jv3b49fj2duo64tjc3j5ttohkk', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vXdWt:8fII-sC58k4UH-azaTKZpYmcHaCifWIAJ6VC-_oAiXQ', '2026-01-05 18:52:43.344666'),
('ae7mkmy8vlyf5lmkfnjbcrrt11afpawd', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vZ15h:IfRyRgDzO6NYQvjicnxe0x2SkfzkjG-sZXZ7B66DNaY', '2026-01-09 14:14:21.463065'),
('r4dp2v57x4u4xw01p3to78m7dbdfsvh8', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vZqvj:WQ4nyBnfiuYQBJNBuj227S0UuI93MfHnna_P-B3bDlI', '2026-01-11 21:35:31.620783'),
('fl3dol1ehvy27rrm3ardrjcgglaar0il', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vZsEK:Tsc-DEFGR2dAecPUG_9MwahnbpA2k80lFs1dyH8CdUM', '2026-01-11 22:58:48.034740'),
('sit5sk4zksu5ekbu7csq3ryfl282w8lw', '.eJzFlkuPmzAUhf9KxDoPMLYxs-y-y67KKPKLxFMwEY9Wo1H-e31Dpko8ZAiTSqyuxD2c-Hz3Js5bsOVdu992ja63RgVPQRQsL58JLn9pCw31wu2uWsvKtrURa5Csz91m_b1Suvh21l4Z7Hmzd28TqgQSIpGhqypBRAnKJVHIlShOJeJYpxLnMg3jnGgUEpLSMNIKM5FjEYJpqW3XOK-fb1lgeamz4GmRBVnWMcHxqcjIFYKZdIXGjEFJcJwFSyc07uj9KzlvFjlfNXujC7XiRdsLSgjRgOTDJyQEncxQnHw0q8EMwvatri76zoar0tgNsNhAe9P3uVI_bkpc8ywTteZK1l0pho_0H0Ifl4uvBj0-Q1MbBb0oDNGQl5b4xinApFnJavcJsl1ddYcRZr1mdmh3BfWRxf6DaMA6TVMKhSUEjigQguPnUt-YjjS1LPToPpOIyhMAzMGPUXaHnz-l8pUfDhtbtUbq23O6Ut0_qa8GH2A4Oaw_GDJkGsfgRhKVP0qQOxRj_E6aeehNDerTowOmlyd6kN4fXciqHF3Ad9ksDCfH9RkmQ6YqFjAYxNnDGyhb89u0r6Nb-K6bh-LUwD5FNnLhPUgRfuuNzasxiv90s1CcHNinmA6YspzCZlPMFUyI5vfcT8MUBbf2s_9KV6pZCE4O6xGMwqGLPpIwj5RK-ugeyqootPumVnaM4oVyFpKTQ_skI3858fE5OP4FZp_-6A:1vbaJ5:kpKRvtI-XBQ5W6gOaMMJ_jYQjJryfzHfqg7Q8x-xEtI', '2026-01-16 16:14:47.255325'),
('lgv0daixqdouyimw1pm1mvy7pjd3810l', '.eJzFlU-PmzAQxb9KxDkJYGwDe-y9x57KKrLHdkILdsQfVdUq371MSKuEJUvYVOI0EvN4zPuNZd68nWibw66tdbXLlffihd76-pkU8FNbbKgfwu7dFpxtqlxuUbK9dOvtV6d08eWivTE4iPrQvc24kkTKGIKuqpgwJbkApkhXwigFIqhOgRpIg8gwTQLGUh6EWtFEGioDNC21bevO6_tb5llR6sx7WWVelrWJFPRcIOwKowl0hUdJgiWmUeatO2Hejd6_YkS9MmJTH3JdqI0oml5QYogaJe--EDNyNiNR_N6sQjMM27faqug7vlBlbn1k4WPb7_tCqW93JV3zIpOVFgqqtpTjI_2H0Kf16rNBT6_Y1LnCXhgEZMxLA70zBZrUG3D7D5DtK9ceJ5j1msWhPRR0iCwaPghHrNM05ViSmOGIkhAc34C-sx3IKyj05HlmIYczACrQL-HJA37DLZW_xfHoW9fkoO_v6Ub1-KY-G3yE4eyww8WwMdMoQjcWK_MsQdGhmOJ31ixDb27QIT0-Yno90ZP0fukCXDl5AP_KFmE4O-6QYTxxVT_JEG-p3Bo3BfGfbhGKswMPKSYjponhuBNOhcJTzs0jN-s4RSms_egvf6NahODssEOC6dgvKgTcR8qBP3sOwRWFhiZ3dorilXIRkrNDD0iGwRAtPb16pz8JBJkR:1vbZef:UDHXrviuldoj_o3cH-D9Rt-xFYeuil0qtKk2dSRiQyE', '2026-01-16 15:33:01.208727'),
('4i1cnjscz3nv8rezt8yq0tsem63eovzm', '.eJzFlkuPmzAUhf9KxDoPMLYxs-y-y67KKPKLxFMwEY9Wo1H-e31Dpko8ZAiTSqyuxD2c-Hz3Js5bsOVdu992ja63RgVPQRQsL58JLn9pCw31wu2uWsvKtrURa5Csz91m_b1Suvh21l4Z7Hmzd28TqgQSIpGhqypBRAnKJVHIlShOJeJYpxLnMg3jnGgUEpLSMNIKM5FjEYJpqW3XOK-fb1lgeamz4GmRBVnWMcHxqcjIFYKZdIXGjEFJcJwFSyc07uj9KzlvFjlfNXujC7XiRdsLSgjRgOTDJyQEncxQnHw0q8EMwvatri76zoar0tgNsNhAe9P3uVI_bkpc8ywTteZK1l0pho_0H0Ifl4uvBj0-Q1MbBb0oDNGQl5b4xinApFnJavcJsl1ddYcRZr1mdmh3BfWRxf6DaMA6TVMKhSUEjigQguPnUt-YjjS1LPToPpOIyhMAzMGPUXaHnz-l8pUfDhtbtUbq23O6Ut0_qa8GH2A4Oaw_GDJkGsfgRhKVP0qQOxRj_E6aeehNDerTowOmlyd6kN4fXciqHF3Ad9ksDCfH9RkmQ6YqFjAYxNnDGyhb89u0r6Nb-K6bh-LUwD5FNnLhPUgRfuuNzasxiv90s1CcHNinmA6YspzCZlPMFUyI5vfcT8MUBbf2s_9KV6pZCE4O6xGMwqGLPpIwj5RK-ugeyqootPumVnaM4oVyFpKTQ_skI3858fE5OP4FZp_-6A:1vccr6:SErhCnAub_VVQ6gyRfejJahqaSHPDjsQzRgY7PSiLfE', '2026-01-19 13:10:12.460278');

-- Table converted from SQLite: myapp_activity
CREATE TABLE IF NOT EXISTS `myapp_activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(128) NOT NULL,
  `text` TEXT NULL,
  `date` DATE NOT NULL,
  `count` BIGINT NOT NULL,
  `total_count` BIGINT NOT NULL,
  `score` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `myapp_activity` (`id`, `title`, `text`, `date`, `count`, `total_count`, `score`) VALUES
(1, '秋季运动会', '秋季运动会，长跑200米,快来参加', '2026-01-05', 0, 20, 100),
(2, '跳远比赛', '跳远比赛跳远比赛跳远比赛跳远比赛跳远比赛跳远比赛', '2026-01-06', 0, 5, 50),
(3, '吃西瓜比赛', '吃西瓜比赛吃西瓜比赛吃西瓜比赛吃西瓜比赛', '2026-01-07', 0, 50, 150);

-- Table converted from SQLite: myapp_area
CREATE TABLE IF NOT EXISTS `myapp_area` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL,
  `desc` VARCHAR(32) NOT NULL,
  `user_id` BIGINT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `myapp_area` (`id`, `name`, `desc`, `user_id`) VALUES
(1, '1单元1号楼', '1#1', 1),
(2, '1单元2号楼', '1#2', 1),
(4, '2单元3号楼', '2#3', 1);

-- Table converted from SQLite: myapp_banner
CREATE TABLE IF NOT EXISTS `myapp_banner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `img` VARCHAR(100) NOT NULL,
  `order` BIGINT NOT NULL,
  `create_time` DATETIME NOT NULL,
  `is_delete` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `myapp_banner` (`id`, `img`, `order`, `create_time`, `is_delete`) VALUES
(1, 'banner/banner1.png', 1, '2025-11-19 10:36:59.538189', 0),
(2, 'banner/banner2.png', 2, '2025-11-19 08:48:46.163672', 0),
(3, 'banner/banner3.png', 3, '2025-11-19 08:48:52.471772', 0);

-- Table converted from SQLite: myapp_collection
CREATE TABLE IF NOT EXISTS `myapp_collection` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL,
  `name_pinyin` VARCHAR(32) NULL,
  `avatar` VARCHAR(100) NOT NULL,
  `create_time` DATETIME NOT NULL,
  `area_id` BIGINT NULL,
  `face_token` VARCHAR(128) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `myapp_collection` (`id`, `name`, `name_pinyin`, `avatar`, `create_time`, `area_id`, `face_token`) VALUES
(1, '刘亦菲', 'liuyifei', 'collection/2025/12/04/lyf.png', '2025-12-18 20:03:35.480025', 1, NULL),
(3, '迪丽热巴', 'dilireba', 'collection/2025/12/09/dlrb2_ygGle6X.png', '2025-12-18 20:03:33.432764', 2, NULL),
(6, 'cyx', NULL, 'collection/2025/12/22/tmp_f6f854fd7b8b33b6468ad13e5157743c29c37779a8434e61.jpg', '2025-12-22 20:13:50.353669', 4, '8d4ae1fd5121ef24bb2d964df1bfe460'),
(7, 'cyxhnb', NULL, 'collection/2025/12/28/tmp_91c5ba517d6aeeeb7680d332c03bd368c0cc35cef871e820.jpg', '2025-12-28 23:03:06.824723', 1, 'deeb6fdbae176fdd81778ed8f40e18c5'),
(8, 'cyx1', NULL, 'collection/2025/12/28/tmp_e093ab3201a4ad88c8634a8f9bc8552ead837235507071de.jpg', '2025-12-28 23:13:45.078518', 2, '1c9179434320262d2af112818fe6b309'),
(9, 'cyxnnn', NULL, 'collection/2025/12/28/tmp_9d0afa97d42f6a94981bb4012fccebb7b56d7d4e9700168c.jpg', '2025-12-28 23:22:28.085921', 4, '243cfb3ffcbc2194ce6ea16520496c4d'),
(10, 'ggg', 'ggg', 'collection/2025/12/28/tmp_0136e35c0827afb7a191f627826a659363406ecb1ccb1478.jpg', '2025-12-28 23:37:38.421326', 2, 'e98528a7b11a0f08f843c03778012f27');

-- Table converted from SQLite: myapp_joinrecord
CREATE TABLE IF NOT EXISTS `myapp_joinrecord` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `exchange` TINYINT(1) NOT NULL,
  `activity_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table converted from SQLite: myapp_notice
CREATE TABLE IF NOT EXISTS `myapp_notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(64) NOT NULL,
  `content` TEXT NOT NULL,
  `img` VARCHAR(100) NOT NULL,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `myapp_notice` (`id`, `title`, `content`, `img`, `create_time`) VALUES
(1, '注意:大型交友会在本社区举行,欢迎大家莅临~~', '注意:大型交友会在本社区举行,欢迎大家莅临~~', 'notice/banner2.png', '2026-01-02 14:10:47.050009'),
(2, '小区中心举行活动', '小区中心举行活动', 'notice/OIP-C.jpg', '2026-01-02 14:19:53.982636'),
(3, '大型交友会', '大型交友会，不限名额', 'notice/shield-3617525_1280.jpg', '2026-01-02 14:20:03.862081');

-- Skipped SQLite table: myapp_userinfo; server MySQL users table is primary.

-- Table converted from SQLite: myapp_welcome
CREATE TABLE IF NOT EXISTS `myapp_welcome` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `img` VARCHAR(100) NOT NULL,
  `order` BIGINT NOT NULL,
  `create_time` DATETIME NOT NULL,
  `is_delete` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT IGNORE INTO `myapp_welcome` (`id`, `img`, `order`, `create_time`, `is_delete`) VALUES
(5, 'welcome/splash2_ELd1yXB.png', 1, '2025-11-18 10:14:53.640919', 0),
(6, 'welcome/splash3_rlCFt5Y.png', 2, '2025-11-18 10:15:01.681700', 0);

SET FOREIGN_KEY_CHECKS=1;