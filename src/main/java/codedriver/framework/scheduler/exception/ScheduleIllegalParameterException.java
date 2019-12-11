package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ScheduleIllegalParameterException extends ApiRuntimeException {
	
	private static final long serialVersionUID = -494019147900767571L;

	public ScheduleIllegalParameterException(String msg) {
		super(msg);
	}
}
