/*
SQLyog Ultimate
MySQL - 5.7.23-log : Database - codedriver_master
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `channel` */

CREATE TABLE `channel` (
  `uuid` varchar(50) NOT NULL COMMENT '全局唯一id，跨环境导入用',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `parent_uuid` varchar(50) NOT NULL COMMENT 'catalog的uuid',
  `is_active` int(1) NOT NULL COMMENT '是否启用',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `color` varchar(50) DEFAULT NULL COMMENT '颜色',
  `desc` text COMMENT '描述',
  `sort` int(11) NOT NULL COMMENT '排序',
  `sla` int(11) DEFAULT NULL,
  `allow_desc` int(1) DEFAULT NULL COMMENT '是否显示上报页描述',
  `help` longtext COMMENT '描述帮助',
  `is_active_help` int(1) DEFAULT NULL COMMENT '是否激活描述帮助',
  `channel_type_uuid` varchar(50) DEFAULT NULL COMMENT '服务类型uuid',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `channel_authority` */

CREATE TABLE `channel_authority` (
  `channel_uuid` varchar(50) NOT NULL COMMENT '服务uuid',
  `type` enum('common','user','team','role') NOT NULL COMMENT '类型',
  `uuid` varchar(50) NOT NULL COMMENT 'uuid',
  KEY `idx_channel_uuid` (`channel_uuid`),
  KEY `idx_uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `channel_priority` */

CREATE TABLE `channel_priority` (
  `channel_uuid` varchar(50) NOT NULL COMMENT 'channel表的uuid',
  `priority_uuid` varchar(50) NOT NULL COMMENT 'priority表的uuid',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:默认优先级,0:否',
  PRIMARY KEY (`channel_uuid`,`priority_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `channel_process` */

CREATE TABLE `channel_process` (
  `channel_uuid` varchar(50) NOT NULL COMMENT 'channel表的uuid',
  `process_uuid` varchar(50) NOT NULL COMMENT 'process表的uuid',
  PRIMARY KEY (`channel_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `channel_type` */

CREATE TABLE `channel_type` (
  `uuid` varchar(50) NOT NULL COMMENT '全局唯一id，跨环境导入用',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `is_active` tinyint(1) DEFAULT NULL COMMENT '是否激活',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `color` varchar(50) DEFAULT NULL COMMENT '颜色',
  `description` text COMMENT '描述',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `prefix` varchar(50) DEFAULT NULL COMMENT '工单号前缀',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `channel_user` */

CREATE TABLE `channel_user` (
  `user_uuid` char(32) NOT NULL COMMENT 'user的uuid',
  `channel_uuid` char(32) NOT NULL COMMENT 'channel的uuid',
  `insert_time` timestamp NULL DEFAULT NULL,
  UNIQUE KEY `user_channel_index` (`user_uuid`,`channel_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `channel_worktime` */

CREATE TABLE `channel_worktime` (
  `channel_uuid` varchar(50) NOT NULL COMMENT 'channel表的uuid',
  `worktime_uuid` varchar(50) NOT NULL COMMENT 'worktime表的uuid',
  PRIMARY KEY (`channel_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
