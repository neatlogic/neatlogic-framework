package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyContentHandlerNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -1521564353159340395L;

	public NotifyContentHandlerNotFoundException(String handler) {
		super("通知内容处理器：'" + handler + "'不存在");
	}
}
