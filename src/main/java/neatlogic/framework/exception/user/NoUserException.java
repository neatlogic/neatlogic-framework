package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NoUserException extends ApiRuntimeException {
	private static final long serialVersionUID = 4864658649046286690L;

	public NoUserException() {
		super("exception.framework.nouserexception");
	}
}
