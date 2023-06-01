package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyParamNameRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = 3346275964221351899L;

    public NotifyPolicyParamNameRepeatException(String name) {
        super("参数名称：“{0}”已存在", name);
    }
}
