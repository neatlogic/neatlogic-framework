package neatlogic.framework.exception.team;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class TeamUserTitleNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -5856346069642288523L;

	public TeamUserTitleNotFoundException(String title) {
		super("组用户头衔：'" + title + "'不存在");
	}
}
