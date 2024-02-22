ALTER TABLE `master_user`
ADD COLUMN `reset_pwd_email_time` timestamp(3) NULL COMMENT '重置密码邮件发送时间' AFTER `is_super_admin`;

ALTER TABLE `master_user_password` ADD UNIQUE INDEX `unique_useruuid`(`user_uuid`) USING BTREE;

ALTER TABLE `master_user_password` ADD UNIQUE INDEX `unique_userid`(`user_id`) USING BTREE;