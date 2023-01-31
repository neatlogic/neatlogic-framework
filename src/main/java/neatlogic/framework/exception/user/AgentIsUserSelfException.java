package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class AgentIsUserSelfException extends ApiRuntimeException {

	private static final long serialVersionUID = 8784255855238341892L;

	public AgentIsUserSelfException() {
		super("请不要授权给自己");
	}
}
