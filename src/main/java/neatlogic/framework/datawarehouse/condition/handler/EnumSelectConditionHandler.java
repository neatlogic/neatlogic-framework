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

package neatlogic.framework.datawarehouse.condition.handler;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.datawarehouse.condition.IDatasourceConditionHandler;
import org.apache.commons.lang3.StringUtils;

public class EnumSelectConditionHandler implements IDatasourceConditionHandler {
    @Override
    public String getExpression(Long fieldId, Object value) {
        if (value != null) {
            if (value instanceof String) {
                String v = value.toString();
                if (StringUtils.isNotBlank(v)) {
                    return "`" + fieldId + "` = '" + v + "'";
                }
            } else if (value instanceof JSONArray) {
                JSONArray valueList = (JSONArray) value;
                String vs = "";
                for (int i = 0; i < valueList.size(); i++) {
                    if (StringUtils.isNotBlank(vs)) {
                        vs += ",";
                    }
                    String v = valueList.getString(i);
                    vs += "'" + v + "'";
                }
                if (StringUtils.isNotBlank(vs)) {
                    return "`" + fieldId + "` in (" + vs + ")";
                }
            }
        }
        return null;
    }

    public String getName() {
        return "enumselect";
    }
}
