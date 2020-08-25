package codedriver.framework.exception.user;

import codedriver.framework.exception.core.ApiRuntimeException;

public class UserNotFoundException extends ApiRuntimeException {
	private static final long serialVersionUID = 3350073094341804788L;

	public UserNotFoundException(String userid) {
		super("用户：" + userid + "不存在");
	}
}
