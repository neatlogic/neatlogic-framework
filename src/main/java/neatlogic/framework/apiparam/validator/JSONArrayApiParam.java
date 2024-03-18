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
