ALTER TABLE `mongodb` ADD COLUMN `auth_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '存放租户管理用于认证的信息' AFTER `option`;

ALTER TABLE `tenant` MODIFY COLUMN `expire_date` timestamp(3) NULL DEFAULT NULL COMMENT '有效期' AFTER `status`;

ALTER TABLE `tenant` MODIFY COLUMN `visit_time` timestamp(3) NULL DEFAULT NULL COMMENT '租户当天第一次访问时间' AFTER `is_need_demo`;

ALTER TABLE `tenant_audit` ADD COLUMN `tenant_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户uuid' AFTER `group_id`;

ALTER TABLE `tenant_audit` DROP COLUMN `tenant_id`;

ALTER TABLE `tenant_module` ADD COLUMN `tenant_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户uuid' FIRST;

ALTER TABLE `tenant_module` ADD COLUMN `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本' AFTER `dml_status`;

ALTER TABLE `tenant_module` DROP PRIMARY KEY;

ALTER TABLE `tenant_module` ADD PRIMARY KEY (`tenant_uuid`, `module_id`) USING BTREE;

ALTER TABLE `tenant_module` DROP COLUMN `tenant_id`;

ALTER TABLE `tenant_module_dmlsql` ADD COLUMN `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型' AFTER `sql_status`;

ALTER TABLE `tenant_module_dmlsql` ADD COLUMN `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误原因' AFTER `type`;

CREATE TABLE `tenant_module_dmlsql_detail`  (
  `hash` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'sql 唯一标识hash',
  `sql` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sql 语句',
  PRIMARY KEY (`hash`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
