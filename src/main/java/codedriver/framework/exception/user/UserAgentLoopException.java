package codedriver.framework.exception.user;

import codedriver.framework.exception.core.ApiRuntimeException;

public class UserAgentLoopException extends ApiRuntimeException {

	private static final long serialVersionUID = 7559156456698135295L;

	public UserAgentLoopException(String userId,String agentId) {
		super("用户：" + agentId + "已代理" + userId + "，不可循环代理");
	}
}
