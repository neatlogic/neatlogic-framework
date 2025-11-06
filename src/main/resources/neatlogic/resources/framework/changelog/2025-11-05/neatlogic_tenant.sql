ALTER TABLE `datawarehouse_datasource`
    MODIFY COLUMN `db_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'mysql' COMMENT '数据库类型' AFTER `expire_unit`;