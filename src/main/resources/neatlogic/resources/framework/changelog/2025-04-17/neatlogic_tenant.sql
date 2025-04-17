ALTER TABLE `team`
    ADD COLUMN `email` varchar(255) NULL COMMENT '邮件地址' AFTER `name`;

ALTER TABLE `team`
    ADD COLUMN `phone` varchar(20) NULL COMMENT '电话' AFTER `name`;