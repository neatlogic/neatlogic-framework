package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

@Deprecated
public class UserAgentLoopException extends ApiRuntimeException {

	private static final long serialVersionUID = 7559156456698135295L;
	@Deprecated
	public UserAgentLoopException(String userId,String agentId) {
		super("用户：" + agentId + "已代理" + userId + "，不可循环代理");
	}
}
