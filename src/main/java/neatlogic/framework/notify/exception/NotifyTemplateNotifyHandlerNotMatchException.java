package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyTemplateNotifyHandlerNotMatchException extends ApiRuntimeException {

    private static final long serialVersionUID = -4188466068213684051L;

    public NotifyTemplateNotifyHandlerNotMatchException(String templateId, String notifyHandlerName) {
        super("exception.framework.notifytemplatenotifyhandlernotmatchexception", templateId, notifyHandlerName);
    }
}
