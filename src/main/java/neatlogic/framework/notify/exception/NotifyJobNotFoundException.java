package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyJobNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 8236167326955410191L;

    public NotifyJobNotFoundException(Long id) {
        super("exception.framework.notifyjobnotfoundexception", id);
    }
}
