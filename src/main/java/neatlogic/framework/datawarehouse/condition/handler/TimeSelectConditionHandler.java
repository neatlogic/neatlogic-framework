/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.datawarehouse.condition.handler;

import neatlogic.framework.datawarehouse.condition.IDatasourceConditionHandler;
import neatlogic.framework.util.TimeUtil;
import com.alibaba.fastjson.JSONObject;
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
