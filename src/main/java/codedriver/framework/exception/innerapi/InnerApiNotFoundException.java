/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.innerapi;

import codedriver.framework.exception.core.ApiRuntimeException;

public class InnerApiNotFoundException extends ApiRuntimeException {
    public InnerApiNotFoundException(String className) {
        super("内部接口“" + className + "”不存在");
    }
}
