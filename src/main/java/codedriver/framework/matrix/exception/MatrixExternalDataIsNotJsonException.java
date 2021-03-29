/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiFieldValidRuntimeException;

public class MatrixExternalDataIsNotJsonException extends ApiFieldValidRuntimeException {

    private static final long serialVersionUID = 7391664365834538416L;

    public MatrixExternalDataIsNotJsonException() {
        super("集成配置接口返回结果不是JSON格式");
    }
}
