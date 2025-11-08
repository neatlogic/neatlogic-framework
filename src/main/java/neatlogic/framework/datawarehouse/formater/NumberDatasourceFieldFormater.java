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
