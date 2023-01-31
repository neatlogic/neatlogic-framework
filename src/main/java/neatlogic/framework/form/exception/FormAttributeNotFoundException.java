/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FormAttributeNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -2778517020600259453L;

    public FormAttributeNotFoundException(String attributeUuid) {
        super("表单属性：'" + attributeUuid + "'不存在");
    }
}
