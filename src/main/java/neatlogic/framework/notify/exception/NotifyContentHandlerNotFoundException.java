package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyContentHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -1521564353159340395L;

    public NotifyContentHandlerNotFoundException(String handler) {
        super("通知内容处理器：“{0}”不存在", handler);
    }
}
