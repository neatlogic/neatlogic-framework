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

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.param.validate.core.ApiParamValidatorBase;
import neatlogic.framework.util.$;

import java.util.regex.Pattern;

public class EmailApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return $.t("common.mailaddress");
    }

    @Override
    public boolean validate(Object param, String rule) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+([_\\.\\-][A-Za-z0-9]+)*@([A-Za-z0-9\\-]+\\.)+[A-Za-z]{2,6}$");
        return pattern.matcher(param.toString()).matches();
    }

    @Override
    public ApiParamType getType() {
        return ApiParamType.EMAIL;
    }

}
