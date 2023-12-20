package neatlogic.framework.exception.auth;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class AuthFailedException extends ApiRuntimeException {

    public AuthFailedException(String authType) {
        super("nff.jsonwebtokenvalidfilter.dofilterinternal.authfailed", authType);
    }
}
