package codedriver.framework.exception.event;

import codedriver.framework.exception.core.ApiRuntimeException;

public class EventTypeNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 6017010319231879310L;

	public EventTypeNotFoundException(Long id) {
		super("事件类型：" + id + "不存在");
	}


}
