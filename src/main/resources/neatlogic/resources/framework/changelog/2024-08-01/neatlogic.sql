CREATE TABLE IF NOT EXISTS `server_run_time` (
    `start_time` TIMESTAMP(3) NOT NULL COMMENT '服务器启动时间',
    `server_id` INT NOT NULL COMMENT '服务器ID',
    `heartbeat_time` TIMESTAMP(3) NOT NULL COMMENT '心跳时间',
    PRIMARY KEY (`start_time`,`server_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务器运行时间表';