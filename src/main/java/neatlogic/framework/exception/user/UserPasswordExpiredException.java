package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.io.Serial;

public class UserPasswordExpiredException extends ApiRuntimeException {

    @Serial
    private static final long serialVersionUID = -1013560026892966987L;

    public UserPasswordExpiredException() {
		super("nfeu.userpasswordexpiredexception.userpasswordexpiredexception");
	}

    public UserPasswordExpiredException(String tmp) {
        super("nfeu.userpasswordexpiredexception.userpasswordexpiredexception");
    }
}
