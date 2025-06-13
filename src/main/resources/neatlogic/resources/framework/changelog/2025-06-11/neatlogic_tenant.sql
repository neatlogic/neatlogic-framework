CREATE TABLE `fulltextindex_dictionary`
(
    `word`      varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `word_hash` char(32) COLLATE utf8mb4_general_ci NOT NULL,
    `id`        bigint                              NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk` (`word_hash`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;