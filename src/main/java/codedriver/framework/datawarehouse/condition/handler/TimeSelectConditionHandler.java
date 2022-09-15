/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.condition.handler;

import codedriver.framework.datawarehouse.condition.IDatasourceConditionHandler;
import codedriver.framework.util.TimeUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static codedriver.framework.util.TimeUtil.YYYY_MM_DD_HH_MM_SS;

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
            String startTime1 = "";
            if (timeRange != null && StringUtils.isNotBlank(timeUnit)) {
                startTime = TimeUtil.timeTransfer(timeRange, timeUnit);
                endTime = format.format(new Date());
            }
            return "`" + fieldId + "`" + ">= STR_TO_DATE('" + startTime + "','%Y-%m-%d %H:%i:%s')" + "and `" + fieldId + "`<= STR_TO_DATE('" + endTime + "','%Y-%m-%d %H:%i:%s')";
        }
        return null;
    }

    public String getName() {
        return "timeselect";
    }
}
