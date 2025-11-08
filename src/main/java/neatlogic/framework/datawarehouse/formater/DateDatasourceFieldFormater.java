/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.datawarehouse.formater;

import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.enums.FieldType;
import neatlogic.framework.datawarehouse.formater.core.IDatasourceFieldFormater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateDatasourceFieldFormater implements IDatasourceFieldFormater {
    private static final DateTimeFormatter MYSQL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat FULL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getType() {
        return FieldType.DATE.getValue();
    }

    @Override
    public Object format(DataSourceFieldVo fieldVo, Object value) {

        if (value == null) return null;

        String str = value.toString().trim();
        if (str.isEmpty()) return null;

        // 1. LocalDate → 格式化
        if (value instanceof LocalDate) {
            return ((LocalDate) value).format(MYSQL_DATE_FORMATTER);
        }

        // 2. LocalDateTime → 取日期部分
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalDate().format(MYSQL_DATE_FORMATTER);
        }

        // 3. java.util.Date
        if (value instanceof Date) {
            return DATE_ONLY_FORMAT.format((Date) value);
        }

        // 4. 数字（时间戳）
        if (str.matches("^\\d+$")) {
            long num = Long.parseLong(str);
            if (num < 10000000000L) { // 秒级时间戳
                num = num * 1000;
            }
            LocalDate date = Instant.ofEpochMilli(num)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            return date.format(MYSQL_DATE_FORMATTER);
        }

        // 5. 完整格式 yyyy-MM-dd HH:mm:ss → 提取日期
        if (str.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            try {
                Date d = FULL_FORMAT.parse(str);
                return DATE_ONLY_FORMAT.format(d);
            } catch (ParseException ignored) {
                return null;
            }
        }

        // 6. 日期格式 yyyy-MM-dd
        if (str.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            try {
                Date d = DATE_ONLY_FORMAT.parse(str);
                return DATE_ONLY_FORMAT.format(d);
            } catch (ParseException ignored) {
                return null;
            }
        }

        // 7. 纯时间（HH:mm:ss）→ 用今天日期
        if (str.matches("^\\d{1,2}:\\d{1,2}(:\\d{1,2})?$")) {
            LocalDate today = LocalDate.now();
            return today.format(MYSQL_DATE_FORMATTER);
        }

        // 8. 无法识别
        return null;
    }
}
