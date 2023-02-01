/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `api`
(
    `token`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                      NOT NULL COMMENT 'token',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                       NULL DEFAULT NULL COMMENT '名称',
    `module_id`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                       NULL DEFAULT NULL COMMENT '模块id',
    `handler`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                      NULL DEFAULT NULL COMMENT '处理器',
    `username`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                       NULL DEFAULT NULL COMMENT '用户名称',
    `password`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                       NULL DEFAULT NULL COMMENT '密码',
    `config`      varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                     NULL DEFAULT NULL COMMENT 'json格式',
    `is_active`   tinyint                                                                            NULL DEFAULT NULL COMMENT '是否启用',
    `expire`      timestamp(3)                                                                       NULL DEFAULT NULL COMMENT '过期时间',
    `description` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                     NULL DEFAULT NULL COMMENT '描述',
    `authtype`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                       NULL DEFAULT NULL COMMENT 'auth认证',
    `type`        enum ('object','stream','binary') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'object' COMMENT '类型',
    `need_audit`  tinyint(1)                                                                         NULL DEFAULT NULL COMMENT '是否记录日志',
    `qps`         double                                                                             NULL DEFAULT 0 COMMENT '每秒访问几次，大于0生效',
    `timeout`     int                                                                                NULL DEFAULT NULL COMMENT '请求时效',
    `create_time` timestamp(3)                                                                       NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`token`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '接口表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `api_access_count`
(
    `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'token',
    `count` int                                                           NULL DEFAULT NULL COMMENT '访问次数',
    PRIMARY KEY (`token`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '接口访问次数表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `api_audit`
(
    `id`               bigint                                                                     NOT NULL COMMENT '记录id',
    `token`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci              NOT NULL COMMENT '接口token',
    `user_uuid`        char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                  NULL DEFAULT NULL COMMENT '访问用户uuid',
    `authtype`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci               NOT NULL COMMENT '认证方式',
    `server_id`        int                                                                        NOT NULL COMMENT '请求处理服务器id',
    `ip`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci               NOT NULL COMMENT '请求ip',
    `start_time`       timestamp(3)                                                               NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`         timestamp(3)                                                               NULL DEFAULT NULL COMMENT '结束时间',
    `time_cost`        bigint                                                                     NULL DEFAULT NULL COMMENT '处理请求耗时',
    `status`           enum ('succeed','failed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
    `param_file_path`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci              NULL DEFAULT NULL COMMENT '参数路径',
    `result_file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci              NULL DEFAULT NULL COMMENT '结果路径',
    `error_file_path`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci              NULL DEFAULT NULL COMMENT '错误路径',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_api_audit_start_time` (`start_time`) USING BTREE,
    INDEX `idx_api_audit_token` (`token`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '接口记录表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `api_audit_detail`
(
    `hash`    char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接口记录内容hash',
    `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接口记录内容',
    PRIMARY KEY (`hash`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '接口记录详情表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `api_audit_path`
(
    `id`            bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '自增唯一标识',
    `path`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '路径',
    `archive_index` int                                                           NULL DEFAULT NULL COMMENT '归档下标',
    `start_index`   bigint                                                        NOT NULL COMMENT '开始位置下标',
    `offset`        bigint                                                        NOT NULL COMMENT '偏移量',
    `server_id`     int                                                           NOT NULL COMMENT '所在服务器ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC;

CREATE TABLE `audit_config`
(
    `name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '审计类型',
    `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci        NULL COMMENT '审计配置',
    PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '控制所有审计的配置信息'
  ROW_FORMAT = Dynamic;

CREATE TABLE `audit_file`
(
    `hash` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '文件hash',
    `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志文件路径',
    PRIMARY KEY (`hash`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '接口文件表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `file`
(
    `id`           bigint                                                        NOT NULL COMMENT 'id',
    `name`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名称',
    `size`         bigint                                                        NOT NULL COMMENT '文件大小',
    `user_uuid`    char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '所属用户，引用user的user_uuid',
    `upload_time`  timestamp(3)                                                  NULL DEFAULT NULL COMMENT '创建时间',
    `type`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '类型',
    `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'mime type',
    `path`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路径',
    `unique_key`   char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT NULL COMMENT '当附件名称需要保持唯一时，需要提供unique_key的md5 hex值',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_size` (`size`) USING BTREE,
    INDEX `idx_upload_time` (`upload_time`) USING BTREE,
    INDEX `idx_name` (`name`, `unique_key`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '上传文件信息表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `filetype_config`
(
    `name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `config` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL COMMENT 'jsong格式，进行允许扩展名等设置',
    PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '文件类型配置表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `form`
(
    `uuid`      char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '表单uuid',
    `name`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表单名字',
    `is_active` tinyint(1)                                                    NOT NULL COMMENT '表单是否启用，1：启用，0：禁用',
    `fcd`       timestamp(3)                                                  NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '表单'
  ROW_FORMAT = Dynamic;

CREATE TABLE `form_attribute`
(
    `form_uuid`        char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '表单uuid',
    `formversion_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '表单版本uuid',
    `uuid`             char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '属性uuid',
    `label`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性名',
    `type`             varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性类型，系统属性不允许修改',
    `handler`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性处理器',
    `config`           mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL COMMENT '属性配置',
    `data`             mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL COMMENT '属性默认值',
    PRIMARY KEY (`form_uuid`, `formversion_uuid`, `uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '表单版本属性'
  ROW_FORMAT = Dynamic;

CREATE TABLE `form_attribute_matrix`
(
    `form_version_uuid`    char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '表单版本uuid',
    `matrix_uuid`          char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '矩阵uuid',
    `form_attribute_label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单组件名称',
    `form_attribute_uuid`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表单组件uuid',
    PRIMARY KEY (`form_version_uuid`, `matrix_uuid`, `form_attribute_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '表单属性引用矩阵关系表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `form_customitem`
(
    `id`                     bigint                                                        NOT NULL COMMENT 'id',
    `name`                   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一标识',
    `label`                  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示名',
    `icon`                   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '图标',
    `is_active`              tinyint(1)                                                    NULL DEFAULT NULL COMMENT '是否激活',
    `config`                 longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL COMMENT '配置,json格式',
    `view_template_config`   longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL COMMENT '表单模板配置',
    `view_template`          longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL COMMENT '表单模板',
    `config_template_config` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL COMMENT '设置模板配置',
    `config_template`        longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL COMMENT '设置模板',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_name` (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;

CREATE TABLE `form_version`
(
    `uuid`        char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表单版本uuid',
    `version`     int                                                       NULL DEFAULT NULL COMMENT '表单版本',
    `is_active`   tinyint(1)                                                NULL DEFAULT NULL COMMENT '表单版本是否启用，1：启用；2：禁用',
    `form_uuid`   char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单uuid',
    `form_config` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '表单配置',
    `fcd`         timestamp(3)                                              NULL DEFAULT NULL COMMENT '第一次创建表单版本时间',
    `fcu`         char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第一次创建表单版本用户',
    `lcd`         timestamp(3)                                              NULL DEFAULT NULL COMMENT '最后一次修改表单版本时间',
    `lcu`         char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '崔后一次修改表单版本用户',
    PRIMARY KEY (`uuid`) USING BTREE,
    INDEX `idx_form_uui` (`form_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '表单版本'
  ROW_FORMAT = Dynamic;

CREATE TABLE `fulltextindex_rebuild_audit`
(
    `type`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          NOT NULL COMMENT '类型',
    `status`     enum ('doing','done') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
    `start_time` timestamp(3)                                                           NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`   timestamp(3)                                                           NULL DEFAULT NULL COMMENT '结束时间',
    `error`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                  NULL COMMENT '异常',
    `editor`     char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci              NULL DEFAULT NULL COMMENT '发起人',
    `server_id`  int                                                                    NULL DEFAULT NULL COMMENT '服务器id',
    PRIMARY KEY (`type`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '索引重建日志'
  ROW_FORMAT = Dynamic;

CREATE TABLE `fulltextindex_word`
(
    `id`   bigint UNSIGNED                                               NOT NULL COMMENT 'id',
    `word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'word',
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'type',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_word` (`word`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'wordbook'
  ROW_FORMAT = Dynamic;

CREATE TABLE `global_lock`
(
    `id`            bigint                                                        NOT NULL COMMENT '主键id',
    `uuid`          char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT NULL COMMENT 'key 散列的唯一标识',
    `key`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'key ',
    `handler`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '使用方',
    `handler_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '使用方入参',
    `is_lock`       tinyint(1)                                                    NULL DEFAULT NULL COMMENT '是否上锁,1:上锁,0:未锁,队列中',
    `description`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '描述',
    `fcd`           timestamp(3)                                                  NULL DEFAULT NULL COMMENT '进队列时间',
    `lcd`           timestamp(3)                                                  NULL DEFAULT NULL COMMENT '上锁时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_uuid` (`uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '全局锁'
  ROW_FORMAT = Dynamic;

CREATE TABLE `integration`
(
    `uuid`      char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci            NOT NULL COMMENT '主键',
    `name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL DEFAULT NULL COMMENT '名称',
    `url`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci        NULL DEFAULT NULL COMMENT 'url',
    `is_active` tinyint(1)                                                           NULL DEFAULT NULL COMMENT '是否激活',
    `handler`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci        NULL DEFAULT NULL COMMENT '处理器类路径',
    `method`    enum ('get','post') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方式',
    `config`    mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          NULL COMMENT '配置',
    `fcd`       timestamp(3)                                                         NULL DEFAULT NULL COMMENT '创建时间',
    `fcu`       char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci            NULL DEFAULT NULL COMMENT '创建人',
    `lcd`       timestamp(3)                                                         NULL DEFAULT NULL COMMENT '修改时间',
    `lcu`       char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci            NULL DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`uuid`) USING BTREE,
    UNIQUE INDEX `idx_name` (`name`) USING BTREE,
    INDEX `idx_fcd` (`fcd`) USING BTREE,
    INDEX `idx_lcd` (`lcd`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '集成配置表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `integration_audit`
(
    `id`               bigint                                                                     NOT NULL COMMENT '记录id',
    `integration_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                  NOT NULL COMMENT '接口token',
    `user_uuid`        char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                  NULL DEFAULT NULL COMMENT '访问用户uuid',
    `request_from`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci               NULL DEFAULT NULL COMMENT '请求方',
    `server_id`        int                                                                        NOT NULL COMMENT '请求处理服务器id',
    `start_time`       timestamp(3)                                                               NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`         timestamp(3)                                                               NULL DEFAULT NULL COMMENT '结束时间',
    `status`           enum ('succeed','failed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
    `param_hash`       char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                  NULL DEFAULT NULL COMMENT '参数hash',
    `result_hash`      char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                  NULL DEFAULT NULL COMMENT '返回内容hash',
    `error_hash`       char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                  NULL DEFAULT NULL COMMENT '错误hash',
    `param_file_path`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci              NULL DEFAULT NULL COMMENT '参数内容文件路径',
    `result_file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci              NULL DEFAULT NULL COMMENT '结果内容文件路径',
    `error_file_path`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci              NULL DEFAULT NULL COMMENT '错误内容文件路径',
    `headers`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                      NULL COMMENT '请求头',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_integration_uuid` (`integration_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'integration_audit'
  ROW_FORMAT = Dynamic;

CREATE TABLE `integration_invoke`
(
    `integration_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '集成配置uuid',
    `invoke_config`    mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'config',
    `invoke_hash`      char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT 'hash',
    PRIMARY KEY (`integration_uuid`, `invoke_hash`) USING BTREE,
    INDEX `idx_invoke_hash` (`invoke_hash`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '集成配置接口调用表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `lock`
(
    `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '专门用来放锁的表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `login_captcha`
(
    `session_id`   char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '登录session_id',
    `code`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
    `expired_time` timestamp                                                     NULL DEFAULT NULL COMMENT '超时时间点',
    PRIMARY KEY (`session_id`) USING BTREE,
    INDEX `idx_expired_time` (`expired_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '登录验证码表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `login_failed_count`
(
    `user_id`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户uuid',
    `failed_count` int                                                           NULL DEFAULT NULL COMMENT '错误次数',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '登录失败次数'
  ROW_FORMAT = Dynamic;

CREATE TABLE `mail_server`
(
    `uuid`         char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '主键id',
    `name`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
    `host`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'host',
    `port`         int                                                           NOT NULL COMMENT '端口',
    `from_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱地址',
    `username`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
    `password`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
    `domain`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '域名',
    PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '邮件服务器表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `matrix`
(
    `id`    bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `uuid`  char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL DEFAULT NULL COMMENT '唯一表示id',
    `name`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '矩阵名称',
    `label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一标识',
    `type`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'custom,自定义 external外部数据',
    `fcu`   char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL DEFAULT NULL COMMENT '创建人',
    `fcd`   timestamp(3)                                                 NULL DEFAULT NULL COMMENT '创建时间',
    `lcu`   char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL DEFAULT NULL COMMENT '修改人',
    `lcd`   timestamp(3)                                                 NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `label` (`label`) USING BTREE,
    UNIQUE INDEX `uuid` (`uuid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 469
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '矩阵信息表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `matrix_attribute`
(
    `id`          bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `uuid`        char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL DEFAULT NULL COMMENT '属性uuid',
    `matrix_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL DEFAULT NULL COMMENT '矩阵uuid',
    `type`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性类型',
    `is_required` tinyint(1)                                                   NULL DEFAULT NULL COMMENT '属性是否必填',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性名称',
    `sort`        int                                                          NULL DEFAULT NULL COMMENT '排序',
    `config`      mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL COMMENT '属性配置',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `matrix_attribute_key` (`matrix_uuid`, `uuid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1081
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '矩阵自定义属性表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `matrix_ci`
(
    `matrix_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '矩阵uuid',
    `ci_id`       bigint                                                    NOT NULL COMMENT 'ci模型id',
    `config`      longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置信息',
    PRIMARY KEY (`matrix_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'cmdb模型矩阵'
  ROW_FORMAT = Dynamic;

CREATE TABLE `matrix_external`
(
    `matrix_uuid`      char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '矩阵uuid',
    `integration_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '插件类型',
    PRIMARY KEY (`matrix_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '矩阵外部数据源配置信息表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `matrix_view`
(
    `matrix_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '矩阵uuid',
    `file_id`     bigint                                                        NULL DEFAULT NULL COMMENT '配置文件',
    `config`      longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT 'config',
    `file_name`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上传配置文件名称',
    `xml`         longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL COMMENT '上传配置文件内容',
    PRIMARY KEY (`matrix_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '矩阵视图表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `menu`
(
    `id`          bigint                                                                NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NOT NULL COMMENT '菜单名称',
    `parent_id`   bigint                                                                NOT NULL DEFAULT 0 COMMENT '父节点id',
    `url`         varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL     DEFAULT NULL COMMENT '菜单url',
    `icon`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL     DEFAULT NULL COMMENT '目录对应的图标class',
    `sort`        int                                                                   NULL     DEFAULT 0 COMMENT '顺序(正序,数字小的靠前',
    `is_active`   int                                                                   NULL     DEFAULT 0 COMMENT '0:正常，1:禁用',
    `module`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL     DEFAULT NULL COMMENT '模块名',
    `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL     DEFAULT NULL COMMENT '菜单描述',
    `is_auto`     tinyint(1)                                                            NULL     DEFAULT 0 COMMENT '是否自动打开，0:否，1:是',
    `open_mode`   enum ('tab','blank') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '打开页面方式，tab:打开新tab页面   blank:打开新标签页',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '菜单表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `menu_mobile`
(
    `name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单标识',
    `label`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名',
    `icon`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
    `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '其他配置',
    `sort`   int                                                           NULL DEFAULT NULL COMMENT '排序',
    PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '移动菜单'
  ROW_FORMAT = Dynamic;

CREATE TABLE `menu_role`
(
    `menu_id`   bigint                                                    NOT NULL COMMENT '菜单Id',
    `role_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色uuid(引用role的uuid)',
    PRIMARY KEY (`menu_id`, `role_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '菜单角色表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `message`
(
    `id`                    bigint                                                        NOT NULL COMMENT '主键id',
    `title`                 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '标题',
    `content`               mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL COMMENT '内容',
    `fcd`                   timestamp(3)                                                  NULL     DEFAULT NULL COMMENT '发送时间',
    `handler`               varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '消息类型处理器全类名',
    `insert_time`           timestamp(3)                                                  NULL     DEFAULT NULL COMMENT '插入时间',
    `trigger`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'active' COMMENT '触发点',
    `notify_policy_handler` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '通知策略处理器全类名',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_insert_time` (`insert_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '消息详情表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `message_recipient`
(
    `message_id` bigint                                                                       NOT NULL COMMENT '消息id',
    `type`       enum ('user','team','role') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接收者类型，用户、组、角色',
    `uuid`       char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                    NOT NULL COMMENT '接收者标识',
    PRIMARY KEY (`uuid`, `message_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '消息接收者表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `message_subscribe`
(
    `user_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                              NOT NULL COMMENT '订阅用户uuid',
    `handler`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                          NOT NULL COMMENT '消息类型处理器全类名',
    `is_active` tinyint(1)                                                                             NULL DEFAULT NULL COMMENT '是否订阅',
    `pop_up`    enum ('shortshow','longshow','close') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '弹框方式，shortshow：临时，longshow：持续，close：不弹框',
    `fcd`       timestamp(3)                                                                           NULL DEFAULT NULL COMMENT '订阅时间',
    PRIMARY KEY (`user_uuid`, `handler`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '消息订阅表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `message_user`
(
    `user_uuid`    char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户uuid',
    `message_id`   bigint                                                    NOT NULL COMMENT '消息id',
    `is_read`      tinyint(1)                                                NULL     DEFAULT 0 COMMENT '1：已读，0：未读',
    `is_show`      tinyint(1)                                                NULL     DEFAULT 0 COMMENT '0：未弹窗，1：已弹窗，2：弹窗关闭',
    `expired_time` timestamp(3)                                              NULL     DEFAULT NULL COMMENT '失效时间',
    `is_delete`    tinyint(1)                                                NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`user_uuid`, `message_id`) USING BTREE,
    INDEX `idx_news_message_id` (`message_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户消息状态表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `mq_subscribe`
(
    `id`          bigint                                                                              NOT NULL COMMENT 'id',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                        NOT NULL COMMENT '唯一标识',
    `topic_name`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                        NULL DEFAULT NULL COMMENT '主题唯一标识',
    `class_name`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                       NULL DEFAULT NULL COMMENT '处理类名',
    `is_durable`  tinyint(1)                                                                          NULL DEFAULT NULL COMMENT '是否持久订阅',
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                       NULL DEFAULT NULL COMMENT '描述',
    `status`      enum ('running','error','pending') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否禁用',
    `is_active`   tinyint(1)                                                                          NULL DEFAULT NULL COMMENT '是否激活',
    `error`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                               NULL COMMENT '异常',
    `config`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                               NULL COMMENT '配置',
    `server_id`   int                                                                                 NULL DEFAULT NULL COMMENT '服务器id',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_name` (`name`) USING BTREE,
    INDEX `idx_classname` (`class_name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'mq订阅表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `mq_topic`
(
    `name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `is_active` tinyint(1)                                                   NULL DEFAULT NULL COMMENT '是否激活',
    PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'mq主题表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `notify_job`
(
    `id`             bigint                                                        NOT NULL COMMENT 'id',
    `name`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '任务名称',
    `cron`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT 'cron',
    `handler`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '插件',
    `notify_handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知方式插件',
    `config`         mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL COMMENT '配置信息',
    `is_active`      tinyint                                                       NOT NULL COMMENT '1：启用；0：禁用',
    `fcd`            timestamp(3)                                                  NULL DEFAULT NULL COMMENT '创建时间',
    `fcu`            char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT NULL COMMENT '创建人',
    `lcd`            timestamp(3)                                                  NULL DEFAULT NULL COMMENT '修改时间',
    `lcu`            char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '通知定时任务表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `notify_job_receiver`
(
    `notify_job_id` bigint                                                                                                          NOT NULL COMMENT '通知定时任务ID',
    `receiver`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                                                   NOT NULL COMMENT '接收者、组、角色uuid或者邮箱',
    `type`          enum ('common','user','team','role','processUserType','email') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'receiver类型，区分是用户、组、角色的uuid还是邮箱',
    `receive_type`  enum ('to','cc') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                                               NOT NULL COMMENT '接收者类型，to:收件人；cc:抄送人',
    PRIMARY KEY (`notify_job_id`, `receiver`, `type`, `receive_type`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '通知定时任务接收者表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `notify_policy`
(
    `id`      bigint                                                        NOT NULL COMMENT '主键',
    `name`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `handler` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '不同类型策略处理器',
    `config`  mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL COMMENT 'json格式配置信息',
    `fcd`     timestamp(3)                                                  NULL DEFAULT NULL COMMENT '创建时间',
    `fcu`     char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT NULL COMMENT '创建人',
    `lcd`     timestamp(3)                                                  NULL DEFAULT NULL COMMENT '修改时间',
    `lcu`     char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '通知策略信息表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `role`
(
    `id`          bigint                                                        NOT NULL COMMENT '自增ID',
    `uuid`        char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT 'uuid',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '角色名称',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色描述',
    PRIMARY KEY (`uuid`) USING BTREE,
    INDEX `id` (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '角色表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `role_authority`
(
    `id`         bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `role_uuid`  char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '角色名称',
    `auth_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '权限组',
    `auth`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_role_auth` (`role_uuid`, `auth_group`, `auth`) USING BTREE,
    INDEX `id` (`id`) USING BTREE,
    INDEX `idx_auth` (`auth`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 804
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '角色授权表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `runner`
(
    `id`            bigint                                                                 NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           NULL     DEFAULT NULL COMMENT '名称',
    `host`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           NOT NULL COMMENT 'ip',
    `port`          int                                                                    NOT NULL COMMENT '端口',
    `url`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          NOT NULL COMMENT 'url',
    `access_key`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           NULL     DEFAULT NULL COMMENT '授权key',
    `access_secret` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          NULL     DEFAULT NULL COMMENT '授权密码',
    `auth_type`     enum ('basic','hmac') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '授权类型',
    `public_key`    varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL     DEFAULT NULL COMMENT 'ssh公钥',
    `private_key`   varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL     DEFAULT NULL COMMENT 'ssh私钥',
    `group_id`      bigint                                                                 NULL     DEFAULT NULL COMMENT '代理分组id',
    `netty_ip`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          NULL     DEFAULT NULL COMMENT '\r\nnettyIp',
    `netty_port`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          NULL     DEFAULT NULL COMMENT 'netty端口',
    `is_delete`     tinyint                                                                NOT NULL DEFAULT 0 COMMENT '是否已删除（0：未删除，1：已删除）',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_host_ip` (`host`, `port`) USING BTREE,
    UNIQUE INDEX `uniq_name` (`name`) USING BTREE,
    INDEX `idx_group_id` (`group_id`) USING BTREE,
    INDEX `idx_is_delete` (`is_delete`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 710084241711117
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'autoexec_runner'
  ROW_FORMAT = Dynamic;

CREATE TABLE `runner_map`
(
    `id`        bigint NOT NULL COMMENT '抽象id',
    `runner_id` bigint NOT NULL COMMENT 'runnerId',
    PRIMARY KEY (`id`, `runner_id`) USING BTREE,
    INDEX `idx_runner_id` (`runner_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'autoexec_runner_map'
  ROW_FORMAT = Dynamic;

CREATE TABLE `runnergroup`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '组名称',
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 710085298675713
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'runner组'
  ROW_FORMAT = Dynamic;

CREATE TABLE `runnergroup_network`
(
    `id`         bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `group_id`   bigint                                                       NULL DEFAULT NULL COMMENT '组id',
    `network_ip` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
    `mask`       tinyint                                                      NULL DEFAULT NULL COMMENT '子网掩码',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 660122690707458
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'autoexec_runnergroup_network'
  ROW_FORMAT = Dynamic;

CREATE TABLE `runnergroup_runner`
(
    `runnergroup_id` bigint NULL DEFAULT NULL COMMENT 'runner组id',
    `runner_id`      bigint NULL DEFAULT NULL COMMENT 'runnerId'
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'runner组和runner关系表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `schedule_job`
(
    `uuid`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '全局唯一id，跨环境导入用',
    `name`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '名称',
    `handler`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '处理器',
    `begin_time` timestamp(3)                                                  NULL     DEFAULT NULL COMMENT '开始时间',
    `end_time`   timestamp(3)                                                  NULL     DEFAULT NULL COMMENT '结束时间',
    `cron`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT 'corn表达式',
    `is_active`  tinyint(1)                                                    NULL     DEFAULT 0 COMMENT '0:禁用，1:激活',
    `need_audit` tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '0:不保存，1:保存',
    `fcd`        timestamp(3)                                                  NULL     DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '定时作业信息表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `schedule_job_audit`
(
    `id`           bigint                                                                               NOT NULL COMMENT 'id',
    `job_uuid`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                        NULL DEFAULT NULL COMMENT '引用schedule_job的uuid',
    `start_time`   timestamp(3)                                                                         NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`     timestamp(3)                                                                         NULL DEFAULT NULL COMMENT '结束时间',
    `content_hash` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                            NULL DEFAULT NULL COMMENT '内容hash',
    `status`       enum ('running','succeed','failed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'running' COMMENT 'success:成功；error异常；processing:进行中',
    `server_id`    int                                                                                  NOT NULL COMMENT 'server id',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_job_uuid` (`job_uuid`) USING BTREE,
    INDEX `idx_end_time` (`end_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '定时作业日志表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `schedule_job_audit_detail`
(
    `hash`    char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'hash',
    `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
    PRIMARY KEY (`hash`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '定时作业日志详情表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `schedule_job_lock`
(
    `job_name`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci               NOT NULL COMMENT 'schedule_job表的uuid',
    `job_group`   varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci               NOT NULL COMMENT 'job组',
    `job_handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci               NULL     DEFAULT NULL COMMENT '处理器',
    `lock`        enum ('running','waiting') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'waiting' COMMENT '定时作业锁',
    `server_id`   int                                                                         NOT NULL COMMENT 'server id',
    PRIMARY KEY (`job_name`, `job_group`) USING BTREE,
    INDEX `idx_server_id` (`server_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '定时作业锁表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `schedule_job_prop`
(
    `id`         bigint                                                        NOT NULL AUTO_INCREMENT COMMENT 'id',
    `job_uuid`   char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '引用schedule_job的uuid',
    `prop_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性名称',
    `prop_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性值',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_jobuuid` (`job_uuid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 142
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '定时作业参数表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `schedule_job_status`
(
    `job_name`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'job uuid',
    `job_group`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'job组',
    `handler`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '处理器',
    `next_fire_time`   timestamp(3)                                                  NULL     DEFAULT NULL COMMENT '下一次被唤醒时间',
    `last_fire_time`   timestamp(3)                                                  NULL     DEFAULT NULL COMMENT '最后一次被唤醒时间',
    `last_finish_time` timestamp(3)                                                  NULL     DEFAULT NULL COMMENT '最后一次完成时间',
    `exec_count`       int                                                           NOT NULL DEFAULT 0 COMMENT '执行次数',
    PRIMARY KEY (`job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '定时作业状态表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `system_notice`
(
    `id`          bigint                                                                                  NOT NULL COMMENT 'id',
    `title`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                            NOT NULL COMMENT '标题',
    `content`     mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                             NOT NULL COMMENT '内容',
    `start_time`  timestamp(3)                                                                            NULL     DEFAULT NULL COMMENT '生效时间',
    `end_time`    timestamp(3)                                                                            NULL     DEFAULT NULL COMMENT '失效时间',
    `status`      enum ('not_issued','issued','stopped') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'not_issued' COMMENT 'not_issued:未下发；issued:已下发；stopped:停用',
    `pop_up`      enum ('longshow','close') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci              NULL     DEFAULT 'close' COMMENT 'longshow:持续弹窗；close:不弹窗',
    `ignore_read` tinyint(1)                                                                              NULL     DEFAULT -1 COMMENT '是否忽略已读，1:是；0:否:-1:不设置',
    `issue_time`  timestamp(3)                                                                            NULL     DEFAULT NULL COMMENT '最近一次下发时间',
    `fcd`         timestamp(3)                                                                            NULL     DEFAULT NULL COMMENT '创建时间',
    `fcu`         char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                               NULL     DEFAULT NULL COMMENT '创建人',
    `lcd`         timestamp(3)                                                                            NULL     DEFAULT NULL COMMENT '修改时间',
    `lcu`         char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                               NULL     DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_issue_time` (`issue_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '系统公告表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `system_notice_recipient`
(
    `system_notice_id` bigint                                                                                NOT NULL COMMENT '系统公告ID',
    `uuid`             char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                             NOT NULL COMMENT '通知对象uuid',
    `type`             enum ('common','user','team','role') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型(common,user,team,role)',
    PRIMARY KEY (`system_notice_id`, `uuid`, `type`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '系统公告通知对象表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `system_notice_user`
(
    `system_notice_id` bigint                                                    NOT NULL COMMENT '系统公告ID',
    `user_uuid`        char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户uuid',
    `is_read`          tinyint(1)                                                NOT NULL COMMENT '1:已读；0:未读',
    PRIMARY KEY (`system_notice_id`, `user_uuid`) USING BTREE,
    INDEX `idx_system_notice_id` (`system_notice_id`) USING BTREE,
    INDEX `idx_user_uuid` (`user_uuid`) USING BTREE,
    INDEX `idx_notice_id_and_user_uuid` (`system_notice_id`, `user_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '系统公告用户接收表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `tag`
(
    `id`   bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签名',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 39
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '标签表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `team`
(
    `id`               bigint                                                       NOT NULL COMMENT 'id',
    `uuid`             char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '全局唯一id，跨环境导入用',
    `name`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `description`      mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL COMMENT '领域组上报描述',
    `parent_uuid`      char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '父节点id',
    `lft`              int                                                          NULL     DEFAULT NULL COMMENT '左编码',
    `rht`              int                                                          NULL     DEFAULT NULL COMMENT '右编码',
    `level`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '级别，如公司，部门',
    `is_delete`        tinyint(1)                                                   NOT NULL DEFAULT 0 COMMENT '是否已删除',
    `upward_uuid_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci        NULL COMMENT '向上uuid路径',
    `upward_name_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci        NULL COMMENT '向上名称路径',
    PRIMARY KEY (`uuid`) USING BTREE,
    INDEX `idx_lft_rht` (`lft`, `rht`) USING BTREE,
    INDEX `idx_rht_lft` (`rht`, `lft`) USING BTREE,
    INDEX `idx_parent_uuid` (`parent_uuid`) USING BTREE,
    INDEX `id` (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '分组信息表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `team_role`
(
    `team_uuid`        char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '引用team的uuid',
    `role_uuid`        char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '引用role的uuid',
    `checked_children` tinyint                                                   NOT NULL COMMENT '是否穿透选择子节点',
    PRIMARY KEY (`team_uuid`, `role_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '分组与角色关系表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `team_user_title`
(
    `team_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组uuid',
    `user_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户uuid',
    `title_id`  bigint                                                    NOT NULL COMMENT '头衔id',
    PRIMARY KEY (`team_uuid`, `user_uuid`, `title_id`) USING BTREE,
    INDEX `idx` (`title_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '分组领导职务关系表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `theme`
(
    `id`     bigint                                                    NOT NULL COMMENT '主键 id',
    `config` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '主题配置',
    `fcu`    varbinary(32)                                             NULL DEFAULT NULL COMMENT '创建人',
    `fcd`    timestamp(3)                                              NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '主题配置表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `user`
(
    `id`        bigint                                                        NOT NULL COMMENT 'ID',
    `uuid`      char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '全局唯一id，跨环境导入用',
    `user_id`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户Id',
    `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '用户名',
    `email`     varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '邮箱',
    `phone`     char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT NULL COMMENT '电话',
    `pinyin`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '拼音',
    `is_active` tinyint(1)                                                    NULL DEFAULT NULL COMMENT '是否激活(1：激活，0：未激活)',
    `user_info` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL COMMENT '额外属性',
    `vip_level` tinyint                                                       NULL DEFAULT 0 COMMENT 'VIP等级(0,1,2,3,4,5)',
    `is_delete` tinyint(1)                                                    NULL DEFAULT 0 COMMENT '是否已删除',
    `token`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '随机生成的令牌，用于hmac认证方式签名',
    `is_super_admin` tinyint(1) DEFAULT NULL COMMENT '是否超级管理员',
    PRIMARY KEY (`uuid`) USING BTREE,
    UNIQUE INDEX `user_id_idx` (`user_id`) USING BTREE,
    INDEX `id` (`id`) USING BTREE,
    INDEX `email` (`email`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户信息表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `user_agent`
(
    `user_uuid`  char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '用户uuid',
    `agent_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '代理人uuid',
    `func`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '功能',
    PRIMARY KEY (`user_uuid`, `agent_uuid`, `func`) USING BTREE,
    INDEX `idx_agent_uuid` (`agent_uuid`) USING BTREE,
    INDEX `idx_user_uuid` (`user_uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户授权代理表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `user_authority`
(
    `id`         bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `user_uuid`  char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NULL DEFAULT NULL COMMENT '用户ID',
    `auth_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '权限组',
    `auth`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_user_auth` (`user_uuid`, `auth_group`, `auth`) USING BTREE,
    INDEX `id` (`id`) USING BTREE,
    INDEX `idx_auth` (`auth`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8704
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户授权表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `user_data`
(
    `user_uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户uuid',
    `data`      longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL COMMENT '数据',
    `type`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '功能类型',
    PRIMARY KEY (`user_uuid`, `type`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户数据'
  ROW_FORMAT = Dynamic;

CREATE TABLE `user_password`
(
    `id`          bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_uuid`   char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '用户uuid',
    `user_id`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
    `password`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户密码',
    `create_time` timestamp                                                    NULL DEFAULT NULL COMMENT '创建时间',
    `is_active`   tinyint(1)                                                   NULL DEFAULT NULL COMMENT '有效性',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8435
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户密码表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `user_profile`
(
    `user_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '用户uuid',
    `module_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块id',
    `config`    mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL COMMENT '个性化 json',
    PRIMARY KEY (`user_uuid`, `module_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户个性化配置'
  ROW_FORMAT = Dynamic;

CREATE TABLE `user_role`
(
    `id`        bigint                                                    NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `user_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户uuid',
    `role_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '引用role的uuid',
    PRIMARY KEY (`user_uuid`, `role_uuid`) USING BTREE,
    INDEX `id` (`id`) USING BTREE,
    INDEX `idx_role_uuid` (`role_uuid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 37434
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '角色成员关系表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `user_session`
(
    `user_uuid`  char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户uuid',
    `visit_time` timestamp(3)                                              NULL DEFAULT NULL COMMENT '访问时间',
    PRIMARY KEY (`user_uuid`) USING HASH
) ENGINE = MEMORY
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户session表'
  ROW_FORMAT = Fixed;

CREATE TABLE `user_team`
(
    `id`        bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `team_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '引用flow_team的uuid',
    `user_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '引用flow_user的uuid',
    `title`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头衔，如组长，副组长',
    PRIMARY KEY (`team_uuid`, `user_uuid`) USING BTREE,
    INDEX `id` (`id`) USING BTREE,
    INDEX `idx_flow_user_team` (`user_uuid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8635
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '分组成员关系表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `user_title`
(
    `id`   bigint                                                       NOT NULL COMMENT '唯一id',
    `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头衔名，如果不被引用则自动删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `unique_name` (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户职务表'
  ROW_FORMAT = Dynamic;

CREATE TABLE `worktime`
(
    `uuid`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
    `name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `is_active` int                                                          NOT NULL DEFAULT 1 COMMENT '是否激活，1：激活，0：禁用',
    `lcu`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '最后修改人',
    `lcd`       timestamp(3)                                                 NULL     DEFAULT NULL COMMENT '最后修改时间',
    `config`    longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '每周的工作时段定义',
    `is_delete` tinyint(1)                                                   NULL     DEFAULT NULL COMMENT '是否已删除',
    PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '服务窗口'
  ROW_FORMAT = Dynamic;

CREATE TABLE `worktime_range`
(
    `worktime_uuid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'worktime表的uuid',
    `year`          int                                                          NOT NULL COMMENT '年份',
    `date`          varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日期',
    `start_time`    bigint                                                       NOT NULL COMMENT '开始时间戳',
    `end_time`      bigint                                                       NOT NULL COMMENT '结束时间戳',
    PRIMARY KEY (`worktime_uuid`, `start_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '服务窗口时间段范围表'
  ROW_FORMAT = Dynamic;
  
  CREATE TABLE `config`  (
    `key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置名',
    `value` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置值',
    `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置描述',
    PRIMARY KEY (`key`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;
  
  CREATE TABLE `datawarehouse_datasource`  (
    `id` bigint NOT NULL COMMENT 'id',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一标识',
    `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '中文名称',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '说明',
    `xml` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'sql语句',
    `is_active` tinyint(1) NULL DEFAULT NULL COMMENT '是否激活',
    `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '定时策略',
    `mode` enum('replace','append') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新模式，追加或替换',
    `expire_count` int NULL DEFAULT NULL COMMENT '过期数值',
    `module_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属模块',
    `status` enum('doing','done','failed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '同步状态',
    `data_count` int NULL DEFAULT NULL COMMENT '数据量',
    `expire_unit` enum('minute','hour','day','month','year') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '过期单位',
    `db_type` enum('mysql','mongodb') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'mysql' COMMENT '数据库类型',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_name`(`name`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据仓库-数据源' ROW_FORMAT = Dynamic;
  
  CREATE TABLE `datawarehouse_datasource_audit`  (
    `id` bigint NOT NULL COMMENT '自增id',
    `datasource_id` bigint NULL DEFAULT NULL COMMENT '数据源id',
    `start_time` datetime(3) NULL DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime(3) NULL DEFAULT NULL COMMENT '结束时间',
    `data_count` int NULL DEFAULT NULL COMMENT '数据量',
    `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_datasource_id`(`datasource_id`) USING BTREE,
    INDEX `idx_end_time`(`end_time`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据仓库-数据源审计' ROW_FORMAT = Dynamic;
  
  CREATE TABLE `datawarehouse_datasource_condition`  (
    `id` bigint NOT NULL COMMENT '主键',
    `datasource_id` bigint NULL DEFAULT NULL COMMENT '数据源Id',
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一标识',
    `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
    `type` enum('text','datetime','time','date','number') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据类型',
    `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '条件值',
    `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置，例如下拉框需要配置',
    `is_required` tinyint NULL DEFAULT NULL COMMENT '是否必填',
    PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据仓库-数据源-条件' ROW_FORMAT = Dynamic;
  
  CREATE TABLE `datawarehouse_datasource_field`  (
    `id` bigint NOT NULL COMMENT '主键',
    `datasource_id` bigint NULL DEFAULT NULL COMMENT '数据源id',
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一标识',
    `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
    `type` enum('text','number','datetime','date','time') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
    `input_type` enum('text','userselect','enumselect','timeselect') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作为条件时的输入方式',
    `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '作为条件时的配置，例如下拉框',
    `is_key` tinyint(1) NULL DEFAULT NULL COMMENT '是否主键',
    `is_condition` tinyint(1) NULL DEFAULT NULL COMMENT '是否作为条件',
    `aggregate` enum('count','sum') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '聚合函数',
    PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据仓库-数据源-字段' ROW_FORMAT = Dynamic;
  
  CREATE TABLE `datawarehouse_datasource_param`  (
    `id` bigint NOT NULL COMMENT '自增id',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
    `label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
    `default_value` bigint NULL DEFAULT NULL COMMENT '默认值',
    `datasource_id` bigint NULL DEFAULT NULL COMMENT '数据源id',
    `current_value` bigint NULL DEFAULT NULL COMMENT '当前值',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_datasource_id`(`datasource_id`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据仓库-数据源-参数' ROW_FORMAT = Dynamic;
  
  CREATE TABLE `dependency`  (
    `from` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '被引用方(上游)标识',
    `type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
    `to` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '下游标识',
    `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '额外信息',
    PRIMARY KEY (`from`, `type`, `to`) USING BTREE,
    INDEX `to_index`(`to`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '全局引用关系表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;