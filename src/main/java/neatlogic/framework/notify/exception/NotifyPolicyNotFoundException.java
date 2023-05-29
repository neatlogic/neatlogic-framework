package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 4427302613682187323L;

    public NotifyPolicyNotFoundException(String path) {
        super("exception.framework.notifypolicynotfoundexception", path);
    }

    public NotifyPolicyNotFoundException(Long id) {
        super("exception.framework.notifypolicynotfoundexception", id);
    }
}
