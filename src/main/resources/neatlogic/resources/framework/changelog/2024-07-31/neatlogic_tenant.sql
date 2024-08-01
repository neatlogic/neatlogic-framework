ALTER TABLE `datawarehouse_datasource`
  CHANGE `status` `status` ENUM('doing','done','failed','aborted') CHARSET utf8mb4 COLLATE utf8mb4_general_ci NULL   COMMENT '同步状态',
  ADD COLUMN `last_fire_time` TIMESTAMP(3) NULL   COMMENT '最后一次激活时间' AFTER `db_type`,
  ADD COLUMN `last_finish_time` TIMESTAMP(3) NULL   COMMENT '最后一次完成时间' AFTER `last_fire_time`,
  ADD COLUMN `next_fire_time` TIMESTAMP(3) NULL   COMMENT '下一次激活时间' AFTER `last_finish_time`;