package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 4427302613682187323L;

    public NotifyPolicyNotFoundException(String path) {
        super("通知策略：“{0}”不存在", path);
    }

    public NotifyPolicyNotFoundException(Long id) {
        super("通知策略：“{0}”不存在", id);
    }
}
