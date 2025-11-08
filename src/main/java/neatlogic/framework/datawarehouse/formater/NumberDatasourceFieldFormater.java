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
import org.apache.commons.lang3.StringUtils;

public class NumberDatasourceFieldFormater implements IDatasourceFieldFormater {
    @Override
    public String getType() {
        return FieldType.NUMBER.getValue();
    }

    @Override
    public Object format(DataSourceFieldVo fieldVo, Object value) {
        if (value == null) {
            return null;
        }

        String str = value.toString().trim();
        if (StringUtils.isBlank(str)) {
            return null;
        }

        // 已经是数字类型
        if (value instanceof Number) {
            return value;
        }

        // 尝试从字符串转为数字
        try {
            if (str.contains(".") || str.contains("e") || str.contains("E")) {
                return Double.parseDouble(str);
            } else {
                // 尝试 long，再不行用 double
                try {
                    return Long.parseLong(str);
                } catch (NumberFormatException e) {
                    return Double.parseDouble(str);
                }
            }
        } catch (NumberFormatException e) {
            // 无法解析的返回 null，避免类型不兼容
            return null;
        }
    }
}
