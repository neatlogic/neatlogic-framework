/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FormNameRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = 1901910086387644808L;

    public FormNameRepeatException(String name) {
        super("表单：'" + name + "'已存在");
    }
}
