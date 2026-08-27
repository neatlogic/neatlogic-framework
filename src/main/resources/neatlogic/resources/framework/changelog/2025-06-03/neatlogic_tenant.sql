ALTER TABLE `datawarehouse_datasource`
  ADD COLUMN `database_id` BIGINT NULL COMMENT '数据源' AFTER `db_type`;

ALTER TABLE `datawarehouse_datasource`
    CHANGE `db_type` `db_type` ENUM ('mysql', 'mongodb', 'jdbc') CHARSET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'mysql' NULL COMMENT '数据库类型';
