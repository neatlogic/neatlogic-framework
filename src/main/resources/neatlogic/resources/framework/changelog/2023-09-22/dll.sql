CREATE TABLE IF NOT EXISTS `api_access_count` (
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'token',
  `count` int DEFAULT NULL COMMENT '访问次数',
  PRIMARY KEY (`token`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='接口访问次数表';