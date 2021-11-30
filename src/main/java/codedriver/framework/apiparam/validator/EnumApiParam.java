/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.apiparam.validator;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.param.validate.core.ApiParamValidatorBase;

public class EnumApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return "枚举";
    }

    @Override
    public boolean validate(Object param, String rule) {
        if (StringUtils.isNotBlank(rule)) {
            if (rule.contains(",")) {
                for (String r : rule.split(",")) {
                    if (param.toString().equalsIgnoreCase(r)) {
                        return true;
                    }
                }
            } else {
                return param.toString().equalsIgnoreCase(rule);
            }
        }
        return false;
    }

    @Override
    public ApiParamType getType() {
        return ApiParamType.ENUM;
    }

}
