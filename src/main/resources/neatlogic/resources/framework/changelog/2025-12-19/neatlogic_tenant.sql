ALTER TABLE `login_failed_count`
ADD COLUMN `last_failed_time` timestamp(3) NULL COMMENT '最近一次失败时间' AFTER `failed_count`;
ALTER TABLE `login_failed_count`
ADD COLUMN `locked_until` timestamp(3) NULL COMMENT '锁定时间' AFTER `last_failed_time`;

ALTER TABLE `login_audit`
    ADD COLUMN `ip` VARCHAR (200) NULL COMMENT 'IP' AFTER `user_uuid`;
ALTER TABLE `login_audit`
  ADD INDEX `idx_ip` (`ip`);
