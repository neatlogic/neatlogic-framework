package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyReferencedCannotBeDeletedException extends ApiRuntimeException {

    private static final long serialVersionUID = -4706990565958078592L;

    public NotifyPolicyReferencedCannotBeDeletedException(String policyId) {
        super("通知策略：“{0}”有被引用，不能删除", policyId);
    }
}
