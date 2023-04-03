package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class UserAuthFailedException extends ApiRuntimeException {
	private static final long serialVersionUID = -1541151756865093959L;

	public UserAuthFailedException() {
		super("exception.framework.userauthfailedexception");
	}
}
