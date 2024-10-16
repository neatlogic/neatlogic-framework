package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyTemplateNameRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = 5519016162524395187L;

    public NotifyTemplateNameRepeatException(String msg) {
        super("通知模板名称：“{0}”已存在", msg);
    }
}
