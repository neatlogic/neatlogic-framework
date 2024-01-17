ALTER TABLE `role` DROP INDEX `idx_env`;

ALTER TABLE `role` ADD COLUMN `rule` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '生效规则' AFTER `description`;

ALTER TABLE `role` DROP COLUMN `env`;