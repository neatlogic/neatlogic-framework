package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiFieldValidRuntimeException;

public class NotifyJobNameRepeatException extends ApiFieldValidRuntimeException {

	private static final long serialVersionUID = -6879855884550571771L;

	public NotifyJobNameRepeatException(String handler) {
		super("通知定时任务：'" + handler + "'已存在");
	}
}
