package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyNameRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = -7472833722050409560L;

    public NotifyPolicyNameRepeatException(String name) {
        super("exception.framework.notifypolicynamerepeatexception", name);
    }
}
