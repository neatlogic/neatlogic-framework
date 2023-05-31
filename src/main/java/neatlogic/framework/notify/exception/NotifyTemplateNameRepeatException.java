package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyTemplateNameRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = 5519016162524395187L;

    public NotifyTemplateNameRepeatException(String msg) {
        super("common.notificat", msg);
    }
}
