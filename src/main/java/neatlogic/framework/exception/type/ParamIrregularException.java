/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ParamIrregularException extends ApiRuntimeException {
    public ParamIrregularException(String paramName) {
        super("参数“" + paramName + "”不符合格式要求");
    }

    public ParamIrregularException(String paramName, String rule) {
        super("参数“" + paramName + "”不符合格式要求， 格式为：" + rule);
    }

    public ParamIrregularException(int index, String paramName, String keyName) {
        super("第：" + index + "个参数“" + paramName + "”的'" + keyName + "'不符合格式要求");
    }
}
