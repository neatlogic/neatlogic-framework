CREATE TABLE  IF NOT EXISTS `form_attribute_data` (
  `id` bigint NOT NULL COMMENT 'id',
  `form_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表单uuid',
  `handler` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
  `attribute_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性名',
  `attribute_uuid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性uuid',
  `data` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '属性值,json格式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='表单实例属性当前值';

CREATE TABLE  IF NOT EXISTS `processtask_formattribute` (
  `processtask_id` bigint NOT NULL COMMENT '工单id',
  `form_attribute_data_id` bigint NOT NULL COMMENT '表单属性值id',
  PRIMARY KEY (`processtask_id`,`form_attribute_data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单与表单属性值关系表';

DROP FUNCTION IF EXISTS `generateSnowflakeId`;

DELIMITER $$
CREATE FUNCTION `generateSnowflakeId`() RETURNS BIGINT
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
    DECLARE rowNum LONG;
    DECLARE currentPage INT DEFAULT 1;
    DECLARE pageSize INT DEFAULT 100;
    DECLARE pageCount INT DEFAULT 0;
    DECLARE yu INT DEFAULT 0;

    SELECT COUNT(1) INTO rowNum FROM `processtask_formattribute_data`;
    IF rowNum > 0 THEN
        SET @lastStamp = -1;
        SET @sequence = 0;

        SET pageCount = CEIL(rowNum / pageSize);
        WHILE currentPage <= pageCount DO
            BEGIN
                DECLARE done INT DEFAULT FALSE;

                DECLARE v_id BIGINT;
                DECLARE v_processTaskId BIGINT;
                DECLARE v_type VARCHAR(50);
                DECLARE v_attributeLabel VARCHAR(50);
                DECLARE v_attributeUuid CHAR(32);
                DECLARE v_formUuid CHAR(32);
                DECLARE v_data MEDIUMTEXT;

                DECLARE startNum INT DEFAULT (currentPage - 1) * pageSize;
                DECLARE cur CURSOR FOR SELECT a.`processtask_id`, a.`type`, a.`attribute_label`, a.`attribute_uuid`, a.`data`, b.`form_uuid`
                                       FROM `processtask_formattribute_data` a
                                       JOIN `processtask_form` b ON b.`processtask_id` = a.`processtask_id`
                                       ORDER BY a.`processtask_id`, a.`sort`
                                       LIMIT startNum, pageSize;

                DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

                SET @batchInsertSql_form_attribute_data = NULL;
                SET @batchInsertSql_processtask_formattribute = NULL;

                OPEN cur;
                read_loop: LOOP
                    FETCH cur INTO v_processTaskId, v_type, v_attributeLabel, v_attributeUuid, v_data, v_formUuid;
                    IF done THEN
                        LEAVE read_loop;
                    END IF;
                    IF v_type IS NULL THEN
			            SET v_type = '';
                    END IF;
                    IF v_attributeLabel IS NULL THEN
                        SET v_attributeLabel = '';
                    END IF;
                    IF v_data IS NULL THEN
                        SET v_data = '';
                    END IF;
                    SET v_id = generateSnowflakeId();
                    IF @batchInsertSql_form_attribute_data IS NULL THEN
                        SET @batchInsertSql_form_attribute_data = CONCAT('INSERT INTO `form_attribute_data` (`id`, `form_uuid`, `handler`, `attribute_label`, `attribute_uuid`, `data`) VALUES', ' (', v_id, ', \'', v_formUuid, '\', \'', v_type, '\', \'', v_attributeLabel, '\', \'', v_attributeUuid, '\', \'', v_data, '\')');
                    ELSE
                        SET @batchInsertSql_form_attribute_data = CONCAT(@batchInsertSql_form_attribute_data, ', (', v_id, ', \'', v_formUuid, '\', \'', v_type, '\', \'', v_attributeLabel, '\', \'', v_attributeUuid, '\', \'', v_data, '\')');
                    END IF;
                    IF @batchInsertSql_processtask_formattribute IS NULL THEN
                        SET @batchInsertSql_processtask_formattribute = CONCAT('INSERT INTO `processtask_formattribute` (`processtask_id`, `form_attribute_data_id`) VALUES', ' (', v_processTaskId, ',', v_id, ')');
                    ELSE
                        SET @batchInsertSql_processtask_formattribute = CONCAT(@batchInsertSql_processtask_formattribute, ', (', v_processTaskId, ',', v_id, ')');
                    END IF;
                END LOOP;
                CLOSE cur;

                PREPARE stmt_1 FROM @batchInsertSql_form_attribute_data;
                EXECUTE stmt_1;
                DEALLOCATE PREPARE stmt_1;
                PREPARE stmt_2 FROM @batchInsertSql_processtask_formattribute;
                EXECUTE stmt_2;
                DEALLOCATE PREPARE stmt_2;

                SET @batchInsertSql_form_attribute_data = NULL;
                SET @batchInsertSql_processtask_formattribute = NULL;
            END;
            SET currentPage = currentPage + 1;
        END WHILE;
    END IF;
END $$
DELIMITER ;

CALL handleProcessTaskFormAttributeData();

DROP PROCEDURE `handleProcessTaskFormAttributeData`;

DROP FUNCTION `generateSnowflakeId`;
