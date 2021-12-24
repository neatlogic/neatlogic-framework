/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.apiparam.validator;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.param.validate.core.ApiParamValidatorBase;
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
