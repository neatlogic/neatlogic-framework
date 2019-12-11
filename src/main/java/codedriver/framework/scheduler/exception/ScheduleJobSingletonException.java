package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ScheduleJobSingletonException extends ApiRuntimeException {

	private static final long serialVersionUID = 1459812716227767055L;

	public ScheduleJobSingletonException(String msg) {
		super(msg);
	}
}
