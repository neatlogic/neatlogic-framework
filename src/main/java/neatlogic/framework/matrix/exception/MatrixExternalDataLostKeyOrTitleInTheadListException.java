/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixExternalDataLostKeyOrTitleInTheadListException extends ApiRuntimeException {

    private static final long serialVersionUID = -4051698659616459991L;

    public MatrixExternalDataLostKeyOrTitleInTheadListException() {
        super("集成配置接口返回结果不符合格式,theadList缺少key或title");
    }
}
