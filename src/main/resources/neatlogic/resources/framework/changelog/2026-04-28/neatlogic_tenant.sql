CREATE TABLE IF NOT EXISTS `module_manage_setting`
(
    `id`     BIGINT   NOT NULL COMMENT '主键ID',
    `config` LONGTEXT NOT NULL COMMENT '配置',
    PRIMARY KEY (`id`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='模块管理显示设置';
