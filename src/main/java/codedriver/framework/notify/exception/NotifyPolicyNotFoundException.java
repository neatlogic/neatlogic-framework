package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 4427302613682187323L;

	public NotifyPolicyNotFoundException(String uuid) {
		super("通知策略：'" + uuid + "'不存在");
	}
}
