/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiFieldValidRuntimeException;

public class MatrixExternalIntegrationHandlerNotFoundException extends ApiFieldValidRuntimeException {

    private static final long serialVersionUID = 8910490897503439883L;

    public MatrixExternalIntegrationHandlerNotFoundException(String handler) {
        super("找不到集成配置处理器：" + handler);
    }
}
