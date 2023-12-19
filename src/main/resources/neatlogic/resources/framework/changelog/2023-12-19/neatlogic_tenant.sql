truncate `user_session`;

ALTER TABLE `user_session` ADD COLUMN `token_hash` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'token哈希值' AFTER `user_uuid`;

ALTER TABLE `user_session` ADD COLUMN `token_create_time` bigint NULL DEFAULT NULL COMMENT 'token创建的时间' AFTER `token_hash`;

ALTER TABLE `user_session` DROP PRIMARY KEY;

ALTER TABLE `user_session` ADD PRIMARY KEY (`token_hash`) USING HASH;

ALTER TABLE `role` ADD COLUMN `env` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生效环境' AFTER `description`;

ALTER TABLE `role` ADD INDEX `idx_env`(`env`) USING BTREE;

CREATE TABLE IF NOT EXISTS `extramenu`
(
    `id`          BIGINT                                                       NOT NULL COMMENT 'id',
    `name`        VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `type`        tinyint(1)                                                   NOT NULL DEFAULT '0' COMMENT '类型，0：目录，1：菜单',
    `is_active`   tinyint(1)                                                   NOT NULL DEFAULT '0' COMMENT '是否激活',
    `url`         VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '跳转链接',
    `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci        DEFAULT NULL COMMENT '描述',
    `parent_id`   BIGINT                                                                DEFAULT NULL COMMENT '父id',
    `lft`         INT                                                                   DEFAULT NULL COMMENT '左编码',
    `rht`         INT                                                                   DEFAULT NULL COMMENT '右编码',
    KEY `idx_lft_rht` (`lft`, `rht`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='附加菜单表';

CREATE TABLE IF NOT EXISTS `extramenu_authority`
(
    `menu_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                          NOT NULL COMMENT '菜单目录id',
    `type`    enum ('common','user','team','role') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
    `uuid`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                          NOT NULL COMMENT 'uuid',
    PRIMARY KEY (`menu_id`, `type`, `uuid`) USING BTREE,
    KEY `idx_uuid` (`uuid`) USING BTREE,
    KEY `idx_menu_id` (`menu_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='附加菜单授权表';