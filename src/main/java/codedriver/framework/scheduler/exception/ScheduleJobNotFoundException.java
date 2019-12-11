package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ScheduleJobNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -1291224813855650235L;

	public ScheduleJobNotFoundException(String msg) {
		super(msg);
	}
}
