CREATE TABLE `database` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `type` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
    `config` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置信息',
    `file_id_list` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '附件id列表',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `datawarehouse_datasource`
  ADD COLUMN `database_id` BIGINT NULL COMMENT '数据源' AFTER `db_type`;