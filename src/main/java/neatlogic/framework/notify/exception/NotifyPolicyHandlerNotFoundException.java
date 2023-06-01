package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 14270890017224092L;

    public NotifyPolicyHandlerNotFoundException(String handler) {
        super("通知策略类型：“{0}”不存在", handler);
    }
}
