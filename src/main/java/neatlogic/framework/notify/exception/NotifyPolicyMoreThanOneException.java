package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyMoreThanOneException extends ApiRuntimeException {

    private static final long serialVersionUID = 2624421172651499894L;

    public NotifyPolicyMoreThanOneException(String handler) {
        super("exception.framework.notifypolicymorethanoneexception", handler);
    }
}
