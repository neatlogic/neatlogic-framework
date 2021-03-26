/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FormReferencedCannotBeDeletedException extends ApiRuntimeException {

    private static final long serialVersionUID = 3459303366397256808L;

    public FormReferencedCannotBeDeletedException(String uuid) {
        super("表单：'" + uuid + "'有被引用，不能删除");
    }
}
