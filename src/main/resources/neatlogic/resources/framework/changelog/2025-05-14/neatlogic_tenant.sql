ALTER TABLE `user_team`
    ADD INDEX `idx_flow_team_uuid`(`team_uuid`) USING BTREE;