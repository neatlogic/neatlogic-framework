CREATE TABLE IF NOT EXISTS `schedule_job_source` (
    `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'schedule_job表的uuid',
    `job_group` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'job组',
    `server_id` int NOT NULL COMMENT '服务ID',
    `server_group` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '服务组',
    `fcu` char(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
    `fcd` timestamp(3) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
