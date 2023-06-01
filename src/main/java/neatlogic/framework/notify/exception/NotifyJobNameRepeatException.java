package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyJobNameRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = -6879855884550571771L;

    public NotifyJobNameRepeatException(String handler) {
        super("通知定时任务：“{0}”已存在", handler);
    }
}
