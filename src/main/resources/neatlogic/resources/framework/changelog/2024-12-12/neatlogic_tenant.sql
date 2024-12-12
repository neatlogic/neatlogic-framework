ALTER TABLE `fulltextindex_rebuild_audit`
    ADD COLUMN `handler` enum ('database','elasticsearch') NULL DEFAULT 'database' COMMENT '处理器' AFTER `server_id`;
ALTER TABLE `fulltextindex_rebuild_audit`
    MODIFY COLUMN `handler` enum('database','elasticsearch') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'database' COMMENT '处理器' AFTER `server_id`,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`type`, `handler`) USING BTREE;