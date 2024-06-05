ALTER TABLE `form_attribute`
    ADD COLUMN `key` VARCHAR(100) NULL   COMMENT '属性英文名' AFTER `uuid`;
ALTER TABLE `form_attribute_data`
    ADD COLUMN `attribute_key` VARCHAR(100) NULL   COMMENT '属性英文名' AFTER `attribute_label`;
ALTER TABLE `form_extend_attribute_data`
    ADD COLUMN `attribute_key` VARCHAR(100) NULL   COMMENT '属性英文名' AFTER `attribute_label`;