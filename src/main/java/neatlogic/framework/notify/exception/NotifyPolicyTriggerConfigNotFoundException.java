package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyTriggerConfigNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -3746256745160304604L;

    public NotifyPolicyTriggerConfigNotFoundException(String id) {
        super("exception.framework.notifypolicytriggerconfignotfoundexception", id);
    }
}
