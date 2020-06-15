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
/*Table structure for table `api` */

CREATE TABLE `api` (
  `token` varchar(50) NOT NULL COMMENT 'token',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `module_id` varchar(50) DEFAULT NULL COMMENT '模块id',
  `handler` varchar(255) DEFAULT NULL COMMENT '处理器',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `config` varchar(4000) DEFAULT NULL COMMENT 'json格式',
  `is_active` tinyint(4) DEFAULT NULL COMMENT '是否启用',
  `expire` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  `description` varchar(4000) DEFAULT NULL COMMENT '描述',
  `authtype` enum('basic','hmac-sha1','token','-','cookie') DEFAULT NULL COMMENT 'auth认证',
  `type` enum('object','stream') DEFAULT 'object' COMMENT '类型',
  `need_audit` tinyint(1) DEFAULT NULL COMMENT '是否记录日志',
  `qps` double DEFAULT '0' COMMENT '每秒访问几次，大于0生效',
  `timeout` int(11) DEFAULT NULL COMMENT '请求时效',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Table structure for table `api_audit` */

CREATE TABLE `api_audit` (
  `uuid` varchar(50) NOT NULL COMMENT '记录uuid',
  `token` varchar(100) NOT NULL COMMENT '接口token',
  `user_uuid` char(32) DEFAULT NULL COMMENT '访问用户uuid',
  `authtype` varchar(50) NOT NULL COMMENT '认证方式',
  `server_id` int(11) NOT NULL COMMENT '请求处理服务器id',
  `ip` varchar(50) NOT NULL COMMENT '请求ip',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `time_cost` bigint(20) DEFAULT NULL COMMENT '处理请求耗时',
  `status` enum('succeed','failed') DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `auth_group` */

CREATE TABLE `auth_group` (
  `name` varchar(255) NOT NULL,
  `desc` varchar(255) DEFAULT NULL COMMENT '权限显示名称',
  `intro` text COMMENT '对该权限的详细介绍',
  `is_active` tinyint(4) DEFAULT '1' COMMENT '若模块不存在，则为0，下次更新该表时将被删除',
  `module_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`),
  UNIQUE KEY `uk_label` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `auth_group_api` */

CREATE TABLE `auth_group_api` (
  `name` varchar(255) NOT NULL,
  `api_component_id` varchar(255) NOT NULL,
  `is_active` tinyint(4) DEFAULT '1',
  PRIMARY KEY (`name`,`api_component_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `auth_group_role` */

CREATE TABLE `auth_group_role` (
  `role_uuid` char(32) NOT NULL,
  `auth_group_name` varchar(255) NOT NULL,
  PRIMARY KEY (`role_uuid`,`auth_group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `catalog` */

CREATE TABLE `catalog` (
  `uuid` varchar(50) NOT NULL COMMENT '全局唯一id，跨环境导入用',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `parent_uuid` varchar(50) DEFAULT NULL COMMENT '父级uuid',
  `is_active` int(1) DEFAULT '1' COMMENT '是否启用',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `color` varchar(50) DEFAULT NULL COMMENT '颜色',
  `desc` text COMMENT '描述',
  `lft` int(11) DEFAULT NULL,
  `rht` int(11) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `catalog_authority` */

CREATE TABLE `catalog_authority` (
  `catalog_uuid` varchar(50) NOT NULL COMMENT '目录uuid',
  `type` enum('common','user','team','role') NOT NULL COMMENT '类型',
  `uuid` varchar(50) NOT NULL COMMENT 'uuid',
  KEY `idx_uuid` (`uuid`),
  KEY `idx_catalog_uuid` (`catalog_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
