CREATE TABLE IF NOT EXISTS `integration_rate_limit`
(
    `integration_uuid`  CHAR(32)     NOT NULL COMMENT '集成配置uuid',
    `window_start_time` TIMESTAMP(3) NULL DEFAULT NULL COMMENT '当前窗口开始时间',
    `counter`           INT          NOT NULL DEFAULT 0 COMMENT '当前窗口已允许调用次数',
    `lcd`               TIMESTAMP(3) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`integration_uuid`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='集成调用限流状态表';
