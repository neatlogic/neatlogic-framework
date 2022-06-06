/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ParamNotExistsException extends ApiRuntimeException {

    public ParamNotExistsException(String paramNames) {
        super("参数：“" + paramNames + "”不能为空");
    }

    public ParamNotExistsException(int index, String keyName) {
        super("第：" + index + "个参数的'" + keyName + "'不能为空");
    }

    public ParamNotExistsException(int index, String paramName, String keyName) {
        super("第：" + index + "个参数'" + paramName + "'的'" + keyName + "'不能为空");
    }

    public ParamNotExistsException(String... paramNames) {
        super("参数：“" + String.join("、", paramNames) + "”不能同时为空");
    }
}
