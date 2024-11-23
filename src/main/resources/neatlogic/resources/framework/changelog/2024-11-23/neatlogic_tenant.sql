ALTER TABLE `mq_topic`
    MODIFY COLUMN `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一标识' FIRST,
    ADD COLUMN `label` varchar(100) NULL COMMENT '名称' AFTER `name`;