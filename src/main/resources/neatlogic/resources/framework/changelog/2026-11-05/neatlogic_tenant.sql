ALTER TABLE `datawarehouse_datasource_audit` MODIFY COLUMN `start_time` timestamp(3) NULL DEFAULT NULL COMMENT '开始时间';
ALTER TABLE `datawarehouse_datasource_audit` MODIFY COLUMN `end_time` timestamp(3) NULL DEFAULT NULL COMMENT '结束时间';
