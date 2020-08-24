package codedriver.framework.exception.event;

import codedriver.framework.exception.core.ApiRuntimeException;

public class EventSolutionNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -3755737935060105949L;

	public EventSolutionNotFoundException(Long id) {
		super("解决方案：" + id + "不存在");
	}


}
