/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FormActiveVersionNotFoundExcepiton extends ApiRuntimeException {

    private static final long serialVersionUID = 6933170692575758579L;

    public FormActiveVersionNotFoundExcepiton(String uuid) {
        super("表单：'" + uuid + "'没有激活版本");
    }
}
