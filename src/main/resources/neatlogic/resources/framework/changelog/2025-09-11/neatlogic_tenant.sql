ALTER TABLE `team_user_title`
ADD COLUMN `title_sort` int NULL COMMENT '头衔排序' AFTER `title_id`;

ALTER TABLE `team_user_title`
ADD INDEX `idx_teamiuud_titlesort`(`team_uuid`, `title_sort`) USING BTREE;