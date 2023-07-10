-- ----------------------------
-- Table structure for matrix_8b237e7b2623429d98d3d4d39976479e
-- ----------------------------
CREATE TABLE IF NOT EXISTS `matrix_8b237e7b2623429d98d3d4d39976479e` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `dda6117e828e4071ad127872f154422f` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `f7a6b24f9d08496f958aa9366fb5e382` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `adbda758e264450d98005373af70bfb8` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `800b534e38fe436aa6fa3e633ae6ee11` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sort` int DEFAULT NULL,
  PRIMARY KEY (`uuid`) USING BTREE,
  KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=303 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;