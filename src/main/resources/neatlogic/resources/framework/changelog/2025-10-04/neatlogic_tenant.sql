ALTER TABLE `worktime_range`
    ADD INDEX `idx_starttime` (`start_time`) USING BTREE,
    ADD INDEX `idx_endtime` (`end_time`) USING BTREE;