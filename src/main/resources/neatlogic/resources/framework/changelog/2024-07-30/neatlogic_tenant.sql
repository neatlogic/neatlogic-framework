CREATE TABLE IF NOT EXISTS `home_page` (
    `id` bigint NOT NULL COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `is_active` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否启用',
    `sort` int NOT NULL COMMENT '排序',
    `config` longtext COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT = '首页设置表';

CREATE TABLE IF NOT EXISTS `home_page_authority` (
    `home_page_id` bigint NOT NULL COMMENT '首页ID',
    `type` enum('common','user','team','role') COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
    `uuid` char(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'UUID',
    PRIMARY KEY (`home_page_id`,`type`,`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='首页设置授权表';