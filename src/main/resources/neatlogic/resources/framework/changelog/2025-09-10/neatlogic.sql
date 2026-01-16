ALTER TABLE `tenant_module_dmlsql`
    ADD COLUMN `ignored` tinyint(1) NULL COMMENT '是否忽略，0:不忽略，1:已忽略，默认忽略' AFTER `fcd`;

ALTER TABLE `tenant_module`
    DROP COLUMN `ddl_status`;

ALTER TABLE `tenant_module`
    DROP COLUMN `dml_status`;