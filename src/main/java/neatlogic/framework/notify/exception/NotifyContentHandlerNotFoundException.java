package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyContentHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -1521564353159340395L;

    public NotifyContentHandlerNotFoundException(String handler) {
        super("exception.framework.notifycontenthandlernotfoundexception", handler);
    }
}
