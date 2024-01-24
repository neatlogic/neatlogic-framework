ALTER TABLE `role`
    ADD COLUMN `is_delete` TINYINT(1) DEFAULT 0  NOT NULL   COMMENT '是否已删除' AFTER `rule`;
