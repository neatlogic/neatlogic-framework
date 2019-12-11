package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ScheduleJobClassNotFoundException extends ApiRuntimeException {

	public ScheduleJobClassNotFoundException(String msg) {
		super(msg);
	}
}
