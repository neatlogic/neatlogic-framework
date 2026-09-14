ALTER TABLE `global_lock` ADD COLUMN `notify_error` LONGTEXT NULL COMMENT '最近一次通知异常';
ALTER TABLE `global_lock` ADD COLUMN `unlock_error` LONGTEXT NULL COMMENT '最近一次解锁异常';
ALTER TABLE `global_lock` ADD COLUMN `notify_revision` BIGINT NOT NULL DEFAULT 0 COMMENT '通知尝试顺序号';
ALTER TABLE `global_lock` ADD COLUMN `unlock_revision` BIGINT NOT NULL DEFAULT 0 COMMENT '解锁尝试顺序号';
CREATE TABLE IF NOT EXISTS `global_lock_operation` (
  `id` CHAR(32) NOT NULL,
  `user_uuid` VARCHAR(64) NOT NULL,
  `lock_id` BIGINT NOT NULL,
  `action` VARCHAR(16) NOT NULL,
  `state` VARCHAR(16) NOT NULL,
  `content` LONGTEXT NOT NULL,
  `fcd` TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `lcd` TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`), KEY `idx_fcd` (`fcd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `global_lock` ADD COLUMN `owner_id` VARCHAR(128) NULL COMMENT '业务归属标识，由处理器解释';
ALTER TABLE `global_lock` ADD INDEX `idx_handler_owner_uuid` (`handler`, `owner_id`, `uuid`);
