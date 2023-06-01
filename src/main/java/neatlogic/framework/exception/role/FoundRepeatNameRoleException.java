package neatlogic.framework.exception.role;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FoundRepeatNameRoleException extends ApiRuntimeException {

    private static final long serialVersionUID = -3099642344615472708L;

    public FoundRepeatNameRoleException(String role) {
        super("存在多个名为：{0}的分组", role);
    }
}
