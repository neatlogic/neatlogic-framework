package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 14270890017224092L;

    public NotifyPolicyHandlerNotFoundException(String handler) {
        super("exception.framework.notifypolicyhandlernotfoundexception", handler);
    }
}
