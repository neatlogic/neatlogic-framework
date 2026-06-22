CREATE TABLE IF NOT EXISTS `feature_usage_audit` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_uuid` CHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户UUID',
    `module_group` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块组',
    `feature_path` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '功能路径',
    `feature_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '功能名称',
    `start_time` TIMESTAMP(3) NOT NULL COMMENT '开始时间',
    `end_time` TIMESTAMP(3) NOT NULL COMMENT '结束时间',
    `duration` BIGINT NOT NULL COMMENT '持续时间（毫秒）',
    `login_audit_id` BIGINT NOT NULL COMMENT '登录记录ID',
    PRIMARY KEY (`id`),
    KEY `idx_login_audit_id` (`login_audit_id`),
    KEY `idx_start_time` (`start_time`)
    ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='功能使用审计';
