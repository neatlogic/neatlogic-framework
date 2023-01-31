package neatlogic.framework.systemnotice.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class SystemNoticeExpiredTimeLessThanActiveTimeException extends ApiRuntimeException {

	private static final long serialVersionUID = 4814546872753015236L;

	public SystemNoticeExpiredTimeLessThanActiveTimeException() {
		super("公告失效时间不能早于生效时间");
	}
}
