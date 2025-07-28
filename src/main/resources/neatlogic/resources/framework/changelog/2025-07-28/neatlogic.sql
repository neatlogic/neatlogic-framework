ALTER TABLE `server_status`
    ADD COLUMN `ip` VARCHAR (50) NULL COMMENT '应用服务器IP' AFTER `heartbeat_time`;