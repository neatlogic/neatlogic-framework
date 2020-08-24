package codedriver.framework.exception.event;

import codedriver.framework.exception.core.ApiRuntimeException;

public class EventSolutionRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = 7578574406227399198L;

	public EventSolutionRepeatException(String name) {
		super("解决方案：" + name + "已存在");
	}


}
