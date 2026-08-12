ALTER TABLE `schedule_job_source`
    ADD COLUMN `lcu` CHAR(32) NOT NULL COMMENT '修改人' AFTER `fcd`,
  ADD COLUMN `lcd` TIMESTAMP(3) NULL COMMENT '修改时间' AFTER `lcu`;
