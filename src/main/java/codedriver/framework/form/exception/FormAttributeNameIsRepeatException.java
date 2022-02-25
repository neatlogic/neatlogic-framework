package codedriver.framework.form.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @author longrf
 * @date 2022/2/21 3:22 下午
 */
public class FormAttributeNameIsRepeatException extends ApiRuntimeException {
    public FormAttributeNameIsRepeatException(String name) {
        super("表单组件名称“" + name + "”重复");
    }
}
