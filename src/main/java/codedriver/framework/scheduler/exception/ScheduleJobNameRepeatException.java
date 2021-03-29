package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ScheduleJobNameRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = 1986043935715158952L;

	public ScheduleJobNameRepeatException(String name){
		super("定时作业：'"+ name + "'已存在");
	}
}
