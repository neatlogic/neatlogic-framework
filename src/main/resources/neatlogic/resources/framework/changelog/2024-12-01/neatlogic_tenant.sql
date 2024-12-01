ALTER TABLE `runner` ADD COLUMN `status` varchar(50) NULL COMMENT '状态' AFTER `is_delete`;
ALTER TABLE `runner` ADD COLUMN `info` text NULL COMMENT '额外信息' AFTER `status`;
ALTER TABLE `runner` ADD COLUMN `status_lcd` timestamp(3) NULL COMMENT '状态更新时间' AFTER `info`;