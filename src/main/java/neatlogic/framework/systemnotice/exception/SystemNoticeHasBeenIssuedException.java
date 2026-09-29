package neatlogic.framework.systemnotice.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class SystemNoticeHasBeenIssuedException extends ApiRuntimeException {

	private static final long serialVersionUID = 3061025905329230055L;

	public SystemNoticeHasBeenIssuedException(String title) {
		super("nfsn.systemnoticehasbeenissuedexception.message", title);
	}
}
