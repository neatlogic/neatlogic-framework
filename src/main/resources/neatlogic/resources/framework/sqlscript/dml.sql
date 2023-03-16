BEGIN;
INSERT INTO `runner` VALUES (1, 'localhost', '127.0.0.1', 8084, 'http://127.0.0.1:8084/autoexecrunner/', NULL, NULL, NULL, NULL, NULL, NULL, '127.0.0.1', '8888', 0);
INSERT INTO `runner_map` VALUES (1, 1);
INSERT INTO `runnergroup` VALUES (477368384757760, '勿动0网段', '测试');
COMMIT;