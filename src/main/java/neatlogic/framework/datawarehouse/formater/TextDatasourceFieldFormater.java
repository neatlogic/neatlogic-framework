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

public class TextDatasourceFieldFormater implements IDatasourceFieldFormater {
    @Override
    public String getType() {
        return FieldType.TEXT.getValue();
    }

    @Override
    public Object format(DataSourceFieldVo fieldVo, Object value) {
        if (value == null) {
            return null;
        }
        String str = value.toString().trim();
        // 空字符串一律返回 null，避免插入空白占位符
        if (StringUtils.isBlank(str)) {
            return null;
        }

        return str;
    }
}
