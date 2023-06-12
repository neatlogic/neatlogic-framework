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

package neatlogic.framework.apiparam.validator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.param.validate.core.ApiParamValidatorBase;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class JSONArrayApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return $.t("common.jsonarray");
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
