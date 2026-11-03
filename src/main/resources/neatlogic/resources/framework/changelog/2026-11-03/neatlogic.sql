ALTER TABLE `mongodb` MODIFY COLUMN `password` TEXT NULL COMMENT '连接密码，支持RSA.AES及历史密文';
ALTER TABLE `elasticsearch` MODIFY COLUMN `password` TEXT NULL COMMENT '连接密码，支持RSA.AES及历史密文';
ALTER TABLE `datasource` MODIFY COLUMN `password` TEXT NULL COMMENT '连接密码，支持RSA.AES及历史密文';
