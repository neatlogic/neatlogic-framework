package neatlogic.framework.exception.auth;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class AuthEncryptException extends ApiRuntimeException {


    public AuthEncryptException(String encrypt) {
        super("nfea.authencryptexception.authencryptexception", encrypt);
    }
}
