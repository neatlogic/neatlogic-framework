CREATE TABLE `schedule_job_load` (
    `job_name` VARCHAR (100) NOT NULL COMMENT 'schedule_job表的uuid',
    `job_group` VARCHAR (100) NOT NULL COMMENT 'job组',
    `server_id` INT NOT NULL COMMENT '服务ID',
    `server_start_time` TIMESTAMP(3) NOT NULL COMMENT '服务启动时间',
    PRIMARY KEY (`job_name`,`job_group`,`server_id`)
) ENGINE = INNODB CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
