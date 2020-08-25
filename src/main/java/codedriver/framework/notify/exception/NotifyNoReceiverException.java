package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyNoReceiverException extends ApiRuntimeException {

	private static final long serialVersionUID = -6888897740938504421L;
	
	public NotifyNoReceiverException() {
		super("没有收件人");
	}
}
