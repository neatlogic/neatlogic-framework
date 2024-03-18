/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
