ALTER TABLE `api`
MODIFY COLUMN `type` enum('object','stream','binary','metric') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'object' COMMENT '类型' AFTER `authtype`;