CREATE TABLE `form_attribute_data` (
  `id` bigint NOT NULL COMMENT 'id',
  `form_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表单uuid',
  `handler` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
  `attribute_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性名',
  `attribute_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性uuid',
  `data` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '属性值,json格式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='表单实例属性当前值';

CREATE TABLE `processtask_formattribute` (
  `processtask_id` bigint NOT NULL COMMENT '工单id',
  `form_attribute_data_id` bigint NOT NULL COMMENT '表单属性值id',
  PRIMARY KEY (`processtask_id`,`form_attribute_data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单与表单属性值关系表';
