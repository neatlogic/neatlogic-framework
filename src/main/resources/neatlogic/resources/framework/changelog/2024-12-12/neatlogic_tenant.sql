ALTER TABLE `fulltextindex_rebuild_audit`
    ADD COLUMN `handler` enum ('database','elasticsearch') NULL DEFAULT 'database' COMMENT '处理器' AFTER `server_id`;
ALTER TABLE `fulltextindex_rebuild_audit`
    DROP PRIMARY KEY;
ALTER TABLE `fulltextindex_rebuild_audit`
    ADD PRIMARY KEY (`type`, `handler`) USING BTREE;