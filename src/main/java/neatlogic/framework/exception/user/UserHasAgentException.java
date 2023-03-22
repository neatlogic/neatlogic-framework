package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class UserHasAgentException extends ApiRuntimeException {

	private static final long serialVersionUID = 5732908853777656246L;

	public UserHasAgentException() {
		super("exception.framework.userhasagentexception");
	}
}
