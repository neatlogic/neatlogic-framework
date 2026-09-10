/* Copyright (C) 2025 TechSure Co., Ltd. All Rights Reserved. */
package neatlogic.framework.form.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 表单保存时携带组件位置、矩阵标识和原始原因的业务异常。 */
public class FormMatrixDataSourceInvalidException extends ApiRuntimeException {
    private static final long serialVersionUID = 1L;

    /** 保留数据源异常作为原因，并通过现有 API 业务异常响应展示可定位的错误。 */
    public FormMatrixDataSourceInvalidException(String componentPath, String matrix, String reason, Throwable cause) {
        super("表单组件【{0}】引用的矩阵【{1}】校验失败：{2}", cause, componentPath, matrix, reason);
    }
}
