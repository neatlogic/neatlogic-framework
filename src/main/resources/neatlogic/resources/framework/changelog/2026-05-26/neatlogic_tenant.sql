-- 记录定时作业配置由哪个应用服务器创建，用于按应用服务分组过滤定时作业管理列表。
ALTER TABLE `schedule_job`
    ADD COLUMN `source_server_id` INT NULL COMMENT '创建该定时作业配置的应用服务器ID' AFTER `need_audit`;
