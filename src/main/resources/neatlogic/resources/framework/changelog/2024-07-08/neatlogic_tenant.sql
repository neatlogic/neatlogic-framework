CREATE TABLE  IF NOT EXISTS `global_lock_pk` (
  `lock_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '锁uuid',
  PRIMARY KEY (`lock_uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='全局锁辅助表，排它锁';