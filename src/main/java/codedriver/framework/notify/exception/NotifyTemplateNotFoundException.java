package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyTemplateNotFoundException extends ApiRuntimeException {
	private static final long serialVersionUID = 5036108230995602750L;

	public NotifyTemplateNotFoundException(String notifyTemplateId) {
		super("找不到id为：" + notifyTemplateId + "的通知模板");
	}
}
