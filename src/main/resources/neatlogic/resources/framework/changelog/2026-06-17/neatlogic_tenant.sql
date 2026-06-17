CREATE TABLE IF NOT EXISTS `feature_usage_audit` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户UUID',
    `module_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模块组',
    `menu_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单路径',
    `menu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
    `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'URL',
    `start_time` timestamp(3) NOT NULL COMMENT '开始时间',
    `end_time` timestamp(3) NOT NULL COMMENT '结束时间',
    `duration` bigint NOT NULL COMMENT '持续时间（毫秒）',
    `login_audit_id` bigint DEFAULT NULL COMMENT '登录记录ID',
    PRIMARY KEY (`id`),
    KEY `idx_login_audit_id` (`login_audit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='功能使用审计';
