package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 4427302613682187323L;

	public NotifyPolicyNotFoundException(String path) {
		super("通知策略：'" + path + "'不存在");
	}

	public NotifyPolicyNotFoundException(Long id) {
		super("通知策略：'" + id + "'不存在");
	}
}
