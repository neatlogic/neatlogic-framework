ALTER TABLE `notify_config`
    DROP PRIMARY KEY;
ALTER TABLE `notify_config`
    ADD COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID' FIRST,
    ADD KEY (`id`);
ALTER TABLE `notify_config`
    ADD PRIMARY KEY (`id`);
ALTER TABLE `notify_config`
    ADD COLUMN `name` VARCHAR (250) NULL COMMENT '名称' AFTER `id`;
ALTER TABLE `notify_config`
    ADD COLUMN `is_active` TINYINT (1) DEFAULT 1 NOT NULL COMMENT '是否激活' AFTER `name`;
ALTER TABLE `notify_config`
    ADD COLUMN `is_default` TINYINT (1) DEFAULT 1 NOT NULL COMMENT '是否是默认配置' AFTER `is_active`;
ALTER TABLE `notify_config`
    CHANGE `id` `id` BIGINT NOT NULL COMMENT '主键ID';
ALTER TABLE `notify_config`
    CHANGE `is_active` `is_active` TINYINT (1) DEFAULT 0 NOT NULL COMMENT '是否激活';
ALTER TABLE `notify_config`
    CHANGE `is_default` `is_default` TINYINT (1) DEFAULT 0 NOT NULL COMMENT '是否是默认配置';