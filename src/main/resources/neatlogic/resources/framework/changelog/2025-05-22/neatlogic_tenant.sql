ALTER TABLE `dependency`
    CHANGE `from` `from` VARCHAR (200) CHARSET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '被引用方(上游)标识';
ALTER TABLE `dependency`
    CHANGE `to` `to` VARCHAR (200) CHARSET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '下游标识';