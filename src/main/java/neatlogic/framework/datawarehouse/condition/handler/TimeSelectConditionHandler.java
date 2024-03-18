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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.datawarehouse.condition.IDatasourceConditionHandler;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static neatlogic.framework.util.TimeUtil.YYYY_MM_DD_HH_MM_SS;

public class TimeSelectConditionHandler implements IDatasourceConditionHandler {
    @Override
    public String getExpression(Long fieldId, Object value) {
        if (value != null) {
            JSONObject object = (JSONObject) value;
            Integer timeRange = object.getInteger("timeRange");
            String timeUnit = object.getString("timeUnit");
            String startTime = object.getString("startTime");
            String endTime = object.getString("endTime");
            SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            if (timeRange != null && StringUtils.isNotBlank(timeUnit)) {
                startTime = TimeUtil.timeTransfer(timeRange, timeUnit);
                endTime = format.format(new Date());
            } else {
                startTime = format.format(new Date(Long.parseLong(String.valueOf(startTime))));
                endTime = format.format(new Date(Long.parseLong(String.valueOf(endTime))));
            }
            return "`" + fieldId + "`" + ">= STR_TO_DATE('" + startTime + "','%Y-%m-%d %H:%i:%s')" + "and `" + fieldId + "`<= STR_TO_DATE('" + endTime + "','%Y-%m-%d %H:%i:%s')";
        }
        return null;
    }

    public String getName() {
        return "timeselect";
    }
}
