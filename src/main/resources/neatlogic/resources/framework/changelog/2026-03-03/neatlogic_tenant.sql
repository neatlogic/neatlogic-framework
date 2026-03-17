CREATE TABLE `embedding_audit`
(
    `target_id`   bigint                                                       NOT NULL COMMENT '目标id',
    `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标类型',
    `create_time` timestamp                                                    NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`target_id`, `target_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='向量化记录表，用于重建向量库时用';