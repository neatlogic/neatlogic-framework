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

public class BooleanApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return "布尔型";
    }

    @Override
    public boolean validate(Object param, String rule) {
        if (param != null && StringUtils.isNotBlank(param.toString())) {
            return Boolean.TRUE.toString().equalsIgnoreCase(param.toString()) || Boolean.FALSE.toString().equalsIgnoreCase(param.toString());
        }
        return true;
    }

    @Override
    public ApiParamType getType() {
        return ApiParamType.BOOLEAN;
    }

}
