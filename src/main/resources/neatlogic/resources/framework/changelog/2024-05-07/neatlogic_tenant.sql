ALTER TABLE `region_team`
DROP COLUMN `type`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`region_id`, `team_uuid`) USING BTREE;