ALTER TABLE `user_session`
ADD INDEX `idx_useruuid`(`user_uuid`) USING BTREE;