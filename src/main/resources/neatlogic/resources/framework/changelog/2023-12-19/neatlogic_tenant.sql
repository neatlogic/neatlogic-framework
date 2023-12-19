truncate `user_session`;

ALTER TABLE `user_session` ADD COLUMN `token_hash` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'token哈希值' AFTER `user_uuid`;

ALTER TABLE `user_session` ADD COLUMN `token_create_time` bigint NULL DEFAULT NULL COMMENT 'token创建的时间' AFTER `token_hash`;

ALTER TABLE `user_session` DROP PRIMARY KEY;

ALTER TABLE `user_session` ADD PRIMARY KEY (`token_hash`) USING HASH;

ALTER TABLE `role` ADD COLUMN `env` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生效环境' AFTER `description`;

ALTER TABLE `role` ADD INDEX `idx_env`(`env`) USING BTREE;