ALTER TABLE `fulltextindex_rebuild_audit`
    MODIFY COLUMN `handler` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'database' COMMENT '处理器' AFTER `server_id`;