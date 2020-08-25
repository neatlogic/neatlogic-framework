package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyReferencedCannotBeDeletedException extends ApiRuntimeException {

	private static final long serialVersionUID = -4706990565958078592L;

	public NotifyPolicyReferencedCannotBeDeletedException(String policyId) {
		super("通知策略：'" + policyId + "'有被引用，不能删除");
	}
}
