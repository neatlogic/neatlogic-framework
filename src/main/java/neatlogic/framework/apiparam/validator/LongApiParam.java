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
import org.apache.commons.lang3.StringUtils;

public class LongApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return $.t("common.long");
    }

    @Override
    public boolean validate(Object param, String rule) {
        if (param != null && StringUtils.isNotBlank(param.toString())) {
            try {
                long i = Long.parseLong(param.toString());
                if (StringUtils.isNotBlank(rule)) {
                    if (rule.contains(",")) {
                        for (String r : rule.split(",")) {
                            try {
                                long ruleNumber = Long.parseLong(r);
                                if (i == ruleNumber) {
                                    return true;
                                }
                            } catch (Exception ignored) {

                            }
                        }
                    } else {
                        try {
                            long ruleNumber = Long.parseLong(rule);
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
        return ApiParamType.LONG;
    }

}
