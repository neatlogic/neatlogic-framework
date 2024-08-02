package neatlogic.framework.exception.auth;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class AuthNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 2541167837356991977L;

    public AuthNotFoundException(String authName) {
        super("nfea.authnotfoundexception.authnotfoundexception.authname", authName);
    }

    public AuthNotFoundException(String apiName, String authName) {
        super("nfea.authnotfoundexception.authnotfoundexception.apiauthname", apiName, authName);
    }
}
