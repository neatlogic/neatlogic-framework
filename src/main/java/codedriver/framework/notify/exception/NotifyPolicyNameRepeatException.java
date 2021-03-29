package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyNameRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = -7472833722050409560L;

	public NotifyPolicyNameRepeatException(String name) {
		super("通知策略：'" + name + "'已存在");
	}
}
