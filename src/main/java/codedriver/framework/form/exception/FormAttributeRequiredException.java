/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FormAttributeRequiredException extends ApiRuntimeException {

    private static final long serialVersionUID = -2778517020610259453L;

    public FormAttributeRequiredException(String label) {
        super("表单属性：'" + label + "'必填");
    }
}
