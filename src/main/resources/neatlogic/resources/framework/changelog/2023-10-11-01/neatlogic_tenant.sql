select * from user;

CREATE TABLE `test1`  (
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '版本',
  `fcd` timestamp(3) NULL DEFAULT NULL COMMENT '创建时间',
  `lcd` timestamp(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`version`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'neatlogic 库版本' ROW_FORMAT = Dynamic;