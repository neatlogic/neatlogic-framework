package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyReferencedCannotBeDeletedException extends ApiRuntimeException {

    private static final long serialVersionUID = -4706990565958078592L;

    public NotifyPolicyReferencedCannotBeDeletedException(String policyId) {
        super("exception.framework.notifypolicyreferencedcannotbedeletedexception", policyId);
    }
}
