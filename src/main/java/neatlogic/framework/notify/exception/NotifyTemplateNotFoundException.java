package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyTemplateNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = 5036108230995602750L;

    public NotifyTemplateNotFoundException(String notifyTemplateId) {
        super("exception.framework.notifytemplatenotfoundexception", notifyTemplateId);
    }
}
