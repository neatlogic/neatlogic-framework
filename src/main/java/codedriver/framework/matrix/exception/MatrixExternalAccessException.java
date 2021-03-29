/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixExternalAccessException extends ApiRuntimeException {

    private static final long serialVersionUID = 4614356458155658127L;

    public MatrixExternalAccessException() {
        super("集成配置接口访问异常");
    }
}
