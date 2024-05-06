CREATE TABLE `region_team` (
  `region_id` bigint NOT NULL COMMENT '地域id',
  `type` enum('owner','worker') COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
  `team_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分组uuid',
  `checked_children` tinyint(1) DEFAULT NULL COMMENT '是否包括子分组',
  `update_time` bigint DEFAULT NULL COMMENT '更新标识',
  PRIMARY KEY (`region_id`,`type`,`team_uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `region` (
  `id` bigint NOT NULL COMMENT '全局唯一id，跨环境导入用',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `parent_id` bigint NOT NULL COMMENT '父级id',
  `worktime_uuid` char(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '服务窗口uuid',
  `is_active` int DEFAULT '1' COMMENT '是否启用',
  `lft` int DEFAULT NULL COMMENT '左编码',
  `rht` int DEFAULT NULL COMMENT '右编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='地域表';