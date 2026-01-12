ALTER TABLE `server_status` ADD COLUMN `start_time` TIMESTAMP(3) NULL COMMENT '服务启动时间' AFTER `status`;
