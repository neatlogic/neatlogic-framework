package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyTemplateNameRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = 5519016162524395187L;
	
	public NotifyTemplateNameRepeatException(String msg) {
		super("通知模板名称：'" + msg + "'已存在");
	}
}
