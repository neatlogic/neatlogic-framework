ALTER TABLE `tagent`
MODIFY COLUMN `os_version` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统版本' AFTER `os_type`;