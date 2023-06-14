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

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.param.validate.core.ApiParamValidatorBase;
import org.apache.commons.lang3.StringUtils;

public class IntegerApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return "整数";
    }

    @Override
    public boolean validate(Object param, String rule) {
        if (param != null && StringUtils.isNotBlank(param.toString())) {
            try {
                int i = Integer.parseInt(param.toString());
                if (StringUtils.isNotBlank(rule)) {
                    if (rule.contains(",")) {
                        for (String r : rule.split(",")) {
                            try {
                                int ruleNumber = Integer.parseInt(r);
                                if (i == ruleNumber) {
                                    return true;
                                }
                            } catch (Exception ignored) {

                            }
                        }
                    } else {
                        try {
                            int ruleNumber = Integer.parseInt(rule);
                            if (i == ruleNumber) {
                                return true;
                            }
                        } catch (Exception ignored) {

                        }
                    }
                    return false;
                }
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ApiParamType getType() {
        return ApiParamType.INTEGER;
    }

}
