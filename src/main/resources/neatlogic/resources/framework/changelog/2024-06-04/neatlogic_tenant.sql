ALTER TABLE `form_attribute`
    ADD COLUMN `key` VARCHAR(100) NULL DEFAULT NULL   COMMENT '属性英文名' AFTER `uuid`;
ALTER TABLE `form_attribute_data`
    ADD COLUMN `attribute_key` VARCHAR(100) NULL DEFAULT NULL   COMMENT '属性英文名' AFTER `attribute_label`;
ALTER TABLE `form_extend_attribute_data`
    ADD COLUMN `attribute_key` VARCHAR(100) NULL DEFAULT NULL   COMMENT '属性英文名' AFTER `attribute_label`;