package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ApiMustBePublicException extends ApiRuntimeException {

    private static final long serialVersionUID = -925235736439720896L;

    public ApiMustBePublicException(String token) {
        super("exception.framework.apimustbepublicexception", token);
    }

}
