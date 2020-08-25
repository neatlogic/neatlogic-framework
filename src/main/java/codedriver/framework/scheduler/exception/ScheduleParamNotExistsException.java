package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ScheduleParamNotExistsException extends ApiRuntimeException {

	private static final long serialVersionUID = 844207022412939884L;

	public ScheduleParamNotExistsException(String className, String paramName) {
		super("定时作业组件：" + className + "的" + paramName + "是必填参数");
	}
}
