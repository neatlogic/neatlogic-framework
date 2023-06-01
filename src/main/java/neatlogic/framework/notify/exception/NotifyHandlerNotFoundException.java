package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 2267881370702764463L;

    public NotifyHandlerNotFoundException(String handler) {
        super("通知处理器：“{0}”不存在", handler);
    }
}
