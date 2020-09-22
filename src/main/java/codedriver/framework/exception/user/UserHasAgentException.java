package codedriver.framework.exception.user;

import codedriver.framework.exception.core.ApiRuntimeException;

public class UserHasAgentException extends ApiRuntimeException {

	private static final long serialVersionUID = 5732908853777656246L;

	public UserHasAgentException() {
		super("请删除当前授权用户再进行新的授权");
	}
}
