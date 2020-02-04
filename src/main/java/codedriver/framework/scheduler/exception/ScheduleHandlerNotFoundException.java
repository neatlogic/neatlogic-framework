package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ScheduleHandlerNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 3900572039277372796L;

	public ScheduleHandlerNotFoundException(String classpath) {
		super("定时作业组件："+ classpath + " 不存在");
	}
}
