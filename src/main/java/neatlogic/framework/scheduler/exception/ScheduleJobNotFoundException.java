package neatlogic.framework.scheduler.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ScheduleJobNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -1291224813855650235L;

	public ScheduleJobNotFoundException(String uuid) {
		super("定时作业：'"+ uuid + "'不存在");
	}
}
