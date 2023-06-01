package neatlogic.framework.exception.role;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class RoleNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -337491984654499964L;

    public RoleNotFoundException(String role) {
        super("角色：{0}不存在", role);
    }
}
