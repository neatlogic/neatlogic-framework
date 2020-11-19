package codedriver.framework.exception.user;

import codedriver.framework.exception.core.ApiRuntimeException;

public class UserIdRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = 5436636984492123266L;

    public UserIdRepeatException(String userId) {
        super("用户ID：'" + userId + "'已存在");
    }
}
