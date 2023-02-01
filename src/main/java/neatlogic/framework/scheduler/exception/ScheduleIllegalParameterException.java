package neatlogic.framework.scheduler.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ScheduleIllegalParameterException extends ApiRuntimeException {

	private static final long serialVersionUID = -494019147900767571L;

	public ScheduleIllegalParameterException(String msg) {
		super("cron表达式格式不正确：" + msg);
	}
}
