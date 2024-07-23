Truncate `user_session`;

ALTER TABLE `user_session`
DROP COLUMN `auth_info`,
ADD COLUMN `auth_info_hash` char(32) NULL COMMENT '用户角色分组信息hash' AFTER `visit_time`,
ADD INDEX `idx_authinfohash`(`auth_info_hash`) USING HASH;

CREATE TABLE `user_session_content` (
  `hash` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '信息hash',
  `content` text COLLATE utf8mb4_general_ci COMMENT '信息',
  PRIMARY KEY (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户会话';
