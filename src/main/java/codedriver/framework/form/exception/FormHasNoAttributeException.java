/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FormHasNoAttributeException extends ApiRuntimeException {

    private static final long serialVersionUID = -5432947213236587326L;

    public FormHasNoAttributeException(String uuid) {
        super("表单：'" + uuid + "'没有属性");
    }
}
