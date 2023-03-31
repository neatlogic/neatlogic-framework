package neatlogic.framework.form.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @author longrf
 * @date 2022/2/21 3:22 下午
 */
public class FormAttributeNameIsRepeatException extends ApiRuntimeException {
    public FormAttributeNameIsRepeatException(String name) {
        super("exception.framework.formattributenameisrepeatexception", name);
    }
}
