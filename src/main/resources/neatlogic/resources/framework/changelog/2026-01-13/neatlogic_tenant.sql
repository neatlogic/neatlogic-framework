CREATE TABLE `user_export_file` (
    `id` bigint NOT NULL COMMENT 'id',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名称',
    `user_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属用户，引用user的user_uuid',
    `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'mime类型',
    `config` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '信息',
    `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读',
    `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
    `start_time` timestamp(3) NOT NULL COMMENT '开始时间',
    `end_time` timestamp(3) NULL DEFAULT NULL COMMENT '结束时间',
    `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路径',
    `size` bigint DEFAULT NULL COMMENT '文件大小',
    `error` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '错误信息',
    PRIMARY KEY (`id`),
    KEY `idx_user_uuid_start_time` (`user_uuid`,`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户导入文件信息表';
