ALTER TABLE `user_session`
ADD INDEX `idx_vistitime`(`visit_time`) USING BTREE;