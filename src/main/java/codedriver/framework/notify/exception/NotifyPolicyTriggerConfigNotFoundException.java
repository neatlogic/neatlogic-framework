package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyPolicyTriggerConfigNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -3746256745160304604L;

	public NotifyPolicyTriggerConfigNotFoundException(String id) {
		super("通知策略触发动作配置：'" + id + "'不存在");
	}
}
