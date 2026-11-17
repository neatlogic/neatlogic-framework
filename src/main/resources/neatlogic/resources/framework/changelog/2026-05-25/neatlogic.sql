ALTER TABLE `server_status`
    ADD COLUMN `server_group` VARCHAR(50) NULL COMMENT '应用服务分组' AFTER `server_id`;
