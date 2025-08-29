ALTER TABLE `extramenu`
    ADD COLUMN `sort` int NULL COMMENT '排序' AFTER `parent_id`;
ALTER TABLE `extramenu`
    ADD COLUMN `open_type` enum ('window','iframe') NULL DEFAULT 'window' COMMENT '打开方式' AFTER `parent_id`;