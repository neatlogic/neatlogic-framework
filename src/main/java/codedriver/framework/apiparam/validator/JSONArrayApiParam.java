/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.apiparam.validator;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.param.validate.core.ApiParamValidatorBase;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class JSONArrayApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return "json数组";
    }

    @Override
    public boolean validate(Object param, String rule) {
        try {
            JSONArray valueList = JSONArray.parseArray(JSONObject.toJSONString(param));
            if (StringUtils.isNotBlank(rule)) {
                if (rule.contains(",")) {
                    List<String> ruleList = Arrays.asList(rule.split(","));
                    for (int i = 0; i < valueList.size(); i++) {
                        if (!ruleList.contains(valueList.getString(i))) {
                            return false;
                        }
                    }
                } else {
                    for (int i = 0; i < valueList.size(); i++) {
                        if (!rule.equalsIgnoreCase(valueList.getString(i))) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public ApiParamType getType() {
        return ApiParamType.JSONARRAY;
    }

}
