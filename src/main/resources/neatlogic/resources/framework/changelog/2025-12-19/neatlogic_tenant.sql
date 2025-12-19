ALTER TABLE `login_audit`
    ADD COLUMN `ip` VARCHAR (200) NULL COMMENT 'IP' AFTER `user_uuid`;
ALTER TABLE `login_audit`
  ADD INDEX `idx_ip` (`ip`);
