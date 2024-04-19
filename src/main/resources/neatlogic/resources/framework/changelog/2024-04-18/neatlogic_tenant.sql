ALTER TABLE `matrix_attribute`
    CHANGE `name` `name` VARCHAR(50) CHARSET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL   COMMENT '属性名称',
    ADD COLUMN `unique_identifier` VARCHAR(50) NOT NULL   COMMENT '属性唯一标识' AFTER `name`;
