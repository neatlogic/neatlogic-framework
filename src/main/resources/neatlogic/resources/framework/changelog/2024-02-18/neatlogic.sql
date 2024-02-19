ALTER TABLE `datasource` DROP INDEX `uk_tenant_uuid`;

ALTER TABLE `datasource` MODIFY COLUMN `tenant_uuid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户uuid' FIRST;

ALTER TABLE `datasource` DROP PRIMARY KEY;

ALTER TABLE `datasource` ADD PRIMARY KEY (`tenant_uuid`) USING BTREE;

ALTER TABLE `datasource` DROP COLUMN `tenant_id`;

ALTER TABLE `master_user` COMMENT = '租户用户';

ALTER TABLE `master_user` ADD COLUMN `is_super_admin` tinyint(1) NULL DEFAULT 0 COMMENT '是否超级管理员' AFTER `is_subscribe`;

ALTER TABLE `master_user` MODIFY COLUMN `uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'uuid' FIRST;

ALTER TABLE `master_user` MODIFY COLUMN `user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id' AFTER `uuid`;

ALTER TABLE `master_user` MODIFY COLUMN `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名' AFTER `user_id`;

ALTER TABLE `master_user` MODIFY COLUMN `pinyin` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拼音' AFTER `user_name`;

ALTER TABLE `master_user` MODIFY COLUMN `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱' AFTER `pinyin`;

ALTER TABLE `master_user` MODIFY COLUMN `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机' AFTER `email`;

ALTER TABLE `master_user` MODIFY COLUMN `is_active` tinyint(1) NULL DEFAULT NULL COMMENT '是否激活' AFTER `phone`;

ALTER TABLE `master_user` MODIFY COLUMN `role` enum('','MASTER_ADMIN','MASTER_MANAGER') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色' AFTER `is_active`;

ALTER TABLE `master_user` MODIFY COLUMN `is_email_active` tinyint(1) NULL DEFAULT NULL COMMENT '是否通过邮件激活，1:已激活 0:未激活 -1:已过期' AFTER `role`;

ALTER TABLE `mongodb` DROP INDEX `uk_tenant_uuid`;

ALTER TABLE `mongodb` DROP PRIMARY KEY;

ALTER TABLE `mongodb` ADD PRIMARY KEY (`tenant_uuid`) USING BTREE;

ALTER TABLE `mongodb` DROP COLUMN `tenant_id`;

ALTER TABLE `tenant` DROP INDEX `uk_uuid`;

ALTER TABLE `tenant` DROP PRIMARY KEY;

ALTER TABLE `tenant` ADD PRIMARY KEY (`uuid`) USING BTREE;

ALTER TABLE `tenant` DROP COLUMN `id`;

ALTER TABLE `tenant_modulegroup` MODIFY COLUMN `tenant_uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户uuid' FIRST;

ALTER TABLE `tenant_modulegroup` DROP PRIMARY KEY;

ALTER TABLE `tenant_modulegroup` ADD PRIMARY KEY (`tenant_uuid`, `module_group`) USING BTREE;

ALTER TABLE `tenant_modulegroup` DROP COLUMN `tenant_id`;