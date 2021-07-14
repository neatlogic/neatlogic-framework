/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ParamNotExistsException extends ApiRuntimeException {
    private static final long serialVersionUID = 9091220382590565470L;

    public ParamNotExistsException(String paramNames) {
        super("参数：“" + paramNames + "”不能为空");
    }

    public ParamNotExistsException(String... paramNames) {
        super("参数：“" + String.join("、", paramNames) + "”不能同时为空");
    }
}
