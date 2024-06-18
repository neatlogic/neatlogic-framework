ALTER TABLE `form_extend_attribute`
    CHANGE `parent_uuid` `parent_uuid` CHAR(32) CHARSET utf8mb4 COLLATE utf8mb4_general_ci NULL   COMMENT '属性父级uuid';