package neatlogic.framework.exception.role;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class RoleNameRepeatException extends ApiRuntimeException {

    public RoleNameRepeatException(String name) {
        super("nfer.rolenamerepeatexception.rolenamerepeatexception", name);
    }
}
