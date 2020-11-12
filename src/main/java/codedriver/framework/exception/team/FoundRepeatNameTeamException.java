package codedriver.framework.exception.team;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FoundRepeatNameTeamException extends ApiRuntimeException {

	private static final long serialVersionUID = -7791153993204463500L;

	public FoundRepeatNameTeamException(String name) {
		super("名为：" + name + "的分组存在多个");
	}
}
