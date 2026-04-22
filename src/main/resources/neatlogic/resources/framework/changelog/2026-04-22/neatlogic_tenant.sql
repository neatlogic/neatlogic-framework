ALTER TABLE `api`
    ADD COLUMN `is_mcp` tinyint(1) NULL DEFAULT 0 COMMENT '是否MCP服务' AFTER `need_audit`;
