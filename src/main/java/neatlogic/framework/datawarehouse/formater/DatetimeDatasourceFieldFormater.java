/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.datawarehouse.formater;

import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.enums.FieldType;
import neatlogic.framework.datawarehouse.formater.core.IDatasourceFieldFormater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DatetimeDatasourceFieldFormater implements IDatasourceFieldFormater {
    private static final DateTimeFormatter MYSQL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat FULL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_ONLY_FORMAT = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String getType() {
        return FieldType.DATETIME.getValue();
    }

    @Override
    public Object format(DataSourceFieldVo fieldVo, Object value) {
        if (value == null) return null;

        String str = value.toString().trim();
        if (str.isEmpty()) return null;

        // 1. LocalDateTime
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(MYSQL_DATETIME_FORMATTER);
        }

        // 2. java.util.Date
        if (value instanceof Date) {
            return FULL_FORMAT.format((Date) value);
        }

        // 3. 数字：时间戳（毫秒或秒）
        if (str.matches("^\\d+$")) {
            long num = Long.parseLong(str);
            if (num < 10000000000L) { // 小于 10 位 => 秒
                num = num * 1000;
            }
            LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(num), ZoneId.systemDefault());
            return dt.format(MYSQL_DATETIME_FORMATTER);
        }

        // 4. 完整格式 yyyy-MM-dd HH:mm:ss
        if (str.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            try {
                Date d = FULL_FORMAT.parse(str);
                return FULL_FORMAT.format(d);
            } catch (ParseException ignored) {
                return null;
            }
        }

        // 5. 仅日期 yyyy-MM-dd
        if (str.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            try {
                Date d = DATE_ONLY_FORMAT.parse(str);
                return FULL_FORMAT.format(d);
            } catch (ParseException ignored) {
                return null;
            }
        }

        // 6. 仅时间 HH:mm:ss
        if (str.matches("^\\d{1,2}:\\d{1,2}(:\\d{1,2})?$")) {
            try {
                Date d = TIME_ONLY_FORMAT.parse(str);
                // 如果只给时间，就拼上今天的日期
                LocalDateTime now = LocalDateTime.now();
                String today = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return today + " " + TIME_ONLY_FORMAT.format(d);
            } catch (ParseException ignored) {
                return null;
            }
        }

        // 7. 其他不识别的格式
        return null;
    }
}
