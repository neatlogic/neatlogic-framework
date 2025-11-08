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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeDatasourceFieldFormater implements IDatasourceFieldFormater {
    private static final DateTimeFormatter MYSQL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String getType() {
        return FieldType.TIME.getValue();
    }

    @Override
    public Object format(DataSourceFieldVo fieldVo, Object value) {
        if (value == null) return null;

        String str = value.toString().trim();
        if (str.isEmpty()) return null;

        // 1. LocalTime → 格式化
        if (value instanceof LocalTime) {
            return ((LocalTime) value).format(MYSQL_TIME_FORMATTER);
        }

        // 2. Date → 提取时分秒
        if (value instanceof Date) {
            return TIME_FORMAT.format((Date) value);
        }

        // 3. 纯数字（秒数 / 毫秒数）
        if (str.matches("^\\d+$")) {
            long num = Long.parseLong(str);
            // 自动判断是否为毫秒
            if (num > 86400) num = num / 1000;
            int hours = (int) ((num / 3600) % 24);
            int minutes = (int) ((num / 60) % 60);
            int seconds = (int) (num % 60);
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        // 4. 已是时间格式 "HH:mm:ss" / "H:m:s"
        if (str.matches("^\\d{1,2}:\\d{1,2}(:\\d{1,2})?$")) {
            try {
                Date d = TIME_FORMAT.parse(str);
                return TIME_FORMAT.format(d);
            } catch (ParseException ignored) {
                return null;
            }
        }
        // 其他无法识别的输入
        return null;
    }
}
