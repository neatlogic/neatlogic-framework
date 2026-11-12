ALTER TABLE `api`
    CHANGE `type` `type` ENUM (
    'object',
    'stream',
    'binary',
    'metric',
    'sse'
    ) CHARSET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'object' NULL COMMENT '类型';
