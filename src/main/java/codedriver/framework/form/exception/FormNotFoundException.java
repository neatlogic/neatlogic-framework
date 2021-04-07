/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FormNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -3973568374981840892L;

    public FormNotFoundException(String uuid) {
        super("表单：'" + uuid + "'不存在");
    }
}