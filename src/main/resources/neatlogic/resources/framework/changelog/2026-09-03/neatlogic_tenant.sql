ALTER TABLE `api_audit`
    ADD COLUMN `type` ENUM ('rest', 'mcp') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'rest' COMMENT '访问类型' AFTER `token`;
