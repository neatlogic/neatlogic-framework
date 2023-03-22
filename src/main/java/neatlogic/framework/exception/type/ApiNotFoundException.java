package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ApiNotFoundException extends ApiRuntimeException {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = -8529977350164125804L;

    public ApiNotFoundException(String token) {
        super("exception.framework.apinotfoundexception", token);
    }
}
