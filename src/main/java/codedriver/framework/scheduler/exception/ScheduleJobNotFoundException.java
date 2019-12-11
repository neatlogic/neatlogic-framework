package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ScheduleJobNotFoundException extends ApiRuntimeException {

	public ScheduleJobNotFoundException(String msg) {
		super(msg);
	}
}
