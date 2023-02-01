package neatlogic.framework.scheduler.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ScheduleJobAuditNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -8420726874503308142L;

	public ScheduleJobAuditNotFoundException(Long auditId) {
		super("定时作业执行记录：" + auditId + "不存在");
	};
}
