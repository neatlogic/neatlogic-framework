/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.type;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.exception.core.ApiRuntimeException;

public class ParamDefaultValueIrregularException extends ApiRuntimeException {
    public ParamDefaultValueIrregularException(String paramName, String defaultValue, ApiParamType paramType) {
        super("参数“" + paramName + "”的默认值“" + defaultValue + "”不符合" + paramType.getText() + "格式");
    }
}
