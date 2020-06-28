package codedriver.framework.exception.team;

import codedriver.framework.exception.core.ApiRuntimeException;

public class TeamLevelNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -6608670031156139058L;

	public TeamLevelNotFoundException(String level) {
		super("组层级：'" + level + "'不存在");
	}
}
