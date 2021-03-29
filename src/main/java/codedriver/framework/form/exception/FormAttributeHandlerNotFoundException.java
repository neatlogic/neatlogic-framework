/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FormAttributeHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 5096527275914516196L;

    public FormAttributeHandlerNotFoundException(String handler) {
        super("表单属性处理器：'" + handler + "'不存在");
    }
}
