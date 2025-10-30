CREATE TABLE `login_audit` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_uuid` char(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户UUID',
    `login_time` timestamp(3) NOT NULL COMMENT '登录时间',
    `login_method` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录方式',
    PRIMARY KEY (`id`),
    KEY `idx_user_uuid` (`user_uuid`),
    KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='登录记录表';

DROP TABLE IF EXISTS `user_login`;

INSERT INTO `audit_config` (`name`, `config`)
VALUES ('LOGIN-AUDIT', '{"timeRange":"1","timeUnit":"month"}');

DELETE FROM `audit_config` WHERE `name` = 'USER-LOGIN-AUDIT';
