ALTER TABLE `processtask_formattribute_data`
  ADD COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT FIRST,
  ADD KEY(`id`),
AUTO_INCREMENT=0;

DROP TABLE IF EXISTS `form_attribute_data`;

CREATE TABLE `form_attribute_data` (
  `id` BIGINT NOT NULL COMMENT 'id',
  `form_uuid` CHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表单uuid',
  `handler` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
  `attribute_label` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性名',
  `attribute_uuid` CHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性uuid',
  `data` MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '属性值,json格式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='表单实例属性当前值';

DROP TABLE IF EXISTS `processtask_formattribute`;

CREATE TABLE `processtask_formattribute` (
  `processtask_id` BIGINT NOT NULL COMMENT '工单id',
  `form_attribute_data_id` BIGINT NOT NULL COMMENT '表单属性值id',
  PRIMARY KEY (`processtask_id`,`form_attribute_data_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单与表单属性值关系表';

DROP FUNCTION IF EXISTS `generateSnowflakeId`;

DELIMITER $$
CREATE FUNCTION `generateSnowflakeId`() RETURNS BIGINT DETERMINISTIC
BEGIN
    DECLARE SCHEDULE_SERVER_ID BIGINT DEFAULT 1;
    DECLARE START_TIMESTAMP BIGINT DEFAULT 1577808000;
    DECLARE SEQUENCE_BIT BIGINT DEFAULT 13;
    DECLARE MACHINE_BIT BIGINT DEFAULT 10;
    DECLARE TIMESTAMP_LEFT BIGINT DEFAULT SEQUENCE_BIT + MACHINE_BIT;
    DECLARE MAX_SEQUENCE BIGINT DEFAULT ~(-1 << SEQUENCE_BIT);
    DECLARE MAX_MACHINE_ID BIGINT DEFAULT ~(-1 << MACHINE_BIT);
    DECLARE machineIdPart BIGINT DEFAULT (SCHEDULE_SERVER_ID & MAX_MACHINE_ID) << SEQUENCE_BIT;

    DECLARE currentStamp BIGINT;
    SET currentStamp = UNIX_TIMESTAMP(NOW());

    IF currentStamp < @lastStamp THEN
        WHILE currentStamp < @lastStamp DO
		    SET currentStamp = UNIX_TIMESTAMP(NOW());
        END WHILE;
    END IF;

    IF currentStamp = @lastStamp THEN
        SET @sequence = (@sequence + 1) & MAX_SEQUENCE;
        IF @sequence = 0 THEN
            WHILE currentStamp < @lastStamp DO
		        SET currentStamp = UNIX_TIMESTAMP(NOW());
            END WHILE;
        END IF;
    ELSE
        SET @sequence = 0;
    END IF;

    SET @lastStamp = currentStamp;

    RETURN ((currentStamp - START_TIMESTAMP) << TIMESTAMP_LEFT) | machineIdPart | @sequence;
END $$

DELIMITER ;

DROP PROCEDURE IF EXISTS `handleProcessTaskFormAttributeData`;

DELIMITER $$
CREATE PROCEDURE handleProcessTaskFormAttributeData()
BEGIN
    DECLARE v_id BIGINT DEFAULT generateSnowflakeId();

    UPDATE `processtask_formattribute_data` SET `id` = `id` + v_id;

    INSERT INTO `form_attribute_data` (`id`, `form_uuid`, `handler`, `attribute_label`, `attribute_uuid`, `data`)
        SELECT
        a.`id`,
        b.`form_uuid`,
        IFNULL(a.`type`, ''),
        IFNULL(a.`attribute_label`, ''),
        a.`attribute_uuid`,
        IFNULL(a.`data`, '')
    FROM `processtask_formattribute_data` a
    JOIN `processtask_form` b ON b.`processtask_id` = a.`processtask_id`
    ORDER BY a.`processtask_id`, a.`sort`;

    INSERT INTO `processtask_formattribute` (`processtask_id`, `form_attribute_data_id`)
    SELECT
        a.`processtask_id`,
        a.`id`
    FROM `processtask_formattribute_data` a
    ORDER BY a.`processtask_id`, a.`sort`;

END $$
DELIMITER ;

CALL handleProcessTaskFormAttributeData();

DROP PROCEDURE `handleProcessTaskFormAttributeData`;

DROP FUNCTION `generateSnowflakeId`;

ALTER TABLE `processtask_formattribute_data`
DROP COLUMN `id`,
DROP INDEX `id`;
