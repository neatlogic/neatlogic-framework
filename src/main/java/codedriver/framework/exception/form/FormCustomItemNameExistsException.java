/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.form;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FormCustomItemNameExistsException extends ApiRuntimeException {
    public FormCustomItemNameExistsException(String name) {
        super("表单自定义组件“" + name + "”已存在");
    }
}
