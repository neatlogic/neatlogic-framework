ALTER TABLE `role`
ADD UNIQUE INDEX `uniq_name`(`name`) USING BTREE;

ALTER TABLE `team`
MODIFY COLUMN `upward_name_path` varchar(768) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '向上名称路径' AFTER `upward_uuid_path`,
ADD INDEX `idx_upwardnamepath`(`upward_name_path`) USING BTREE;

ALTER TABLE `region`
MODIFY COLUMN `upward_name_path` varchar(768) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所有父节点name路径' AFTER `upward_id_path`,
ADD INDEX `idx_name`(`name`) USING BTREE,
ADD INDEX `idx_parentid`(`parent_id`) USING BTREE,
ADD INDEX `idx_upwardnamepath`(`upward_name_path`) USING BTREE,
ADD INDEX `idx_lftrht`(`lft`, `rht`) USING BTREE,
ADD INDEX `idx_rgtlft`(`rht`, `lft`) USING BTREE;