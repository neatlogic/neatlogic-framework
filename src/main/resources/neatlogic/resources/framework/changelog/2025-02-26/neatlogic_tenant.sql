CREATE TABLE `schedule_job_data`
(
    `job_uuid`   char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '作业唯一id',
    `data_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性名',
    `data_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '属性值',
    PRIMARY KEY (`job_uuid`, `data_name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='定时作业过程数据';