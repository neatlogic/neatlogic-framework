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
            String str = null;
            if (param instanceof String) {
                str = param.toString();
            } else {
                str = JSONObject.toJSONString(param);
            }
            JSONArray valueList = JSONArray.parseArray(str);
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
