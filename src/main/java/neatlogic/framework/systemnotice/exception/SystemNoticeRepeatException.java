package neatlogic.framework.systemnotice.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class SystemNoticeRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = -2920759826346310211L;

	public SystemNoticeRepeatException(String title) {
		super("nfsn.systemnoticerepeatexception.message", title);
	}
}
