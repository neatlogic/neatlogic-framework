/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FormVersionNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -3137999499740470766L;

    public FormVersionNotFoundException(String uuid) {
        super("表单版本：'" + uuid + "'不存在");
    }
}
