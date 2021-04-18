/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixExternalIntegrationUuidEmptyException extends ApiRuntimeException {

    private static final long serialVersionUID = -6114648427319986274L;

    public MatrixExternalIntegrationUuidEmptyException() {
        super("integrationUuid不能为空");
    }
}