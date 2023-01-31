package neatlogic.framework.scheduler.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ScheduleJobSingletonException extends ApiRuntimeException {

	private static final long serialVersionUID = 1459812716227767055L;

	public ScheduleJobSingletonException(String classpath) {
		super("定时作业组件：'"+ classpath + "' 只能创建一个作业");
	}
}
