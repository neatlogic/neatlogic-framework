package codedriver.framework.exception.user;

import codedriver.framework.exception.core.ApiRuntimeException;

public class UserAgentRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = 5732908853777656246L;

	public UserAgentRepeatException(String userId) {
		super("用户：" + userId + "已存在代理关系");
	}
}
