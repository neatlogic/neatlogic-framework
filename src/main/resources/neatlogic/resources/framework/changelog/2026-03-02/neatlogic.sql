CREATE TABLE `qdrant`
(
    `tenant_uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户uuid',
    `api_key`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT 'api key',
    `url`        varchar(1025) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '访问地址',
    `config`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '选择项',
    PRIMARY KEY (`tenant_uuid`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;