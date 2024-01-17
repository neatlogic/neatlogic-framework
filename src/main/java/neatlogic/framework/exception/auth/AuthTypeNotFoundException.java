package neatlogic.framework.exception.auth;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class AuthTypeNotFoundException extends ApiRuntimeException {

    public AuthTypeNotFoundException(String authType) {
        super("nff.jsonwebtokenvalidfilter.dofilterinternal.authtypeinvalid", authType);
    }
}
