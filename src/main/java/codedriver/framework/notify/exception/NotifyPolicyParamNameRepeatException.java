package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiFieldValidRuntimeException;

public class NotifyPolicyParamNameRepeatException extends ApiFieldValidRuntimeException {

	private static final long serialVersionUID = 3346275964221351899L;

	public NotifyPolicyParamNameRepeatException(String name) {
		super("参数名称：'" + name + "'已存在");
	}
}
