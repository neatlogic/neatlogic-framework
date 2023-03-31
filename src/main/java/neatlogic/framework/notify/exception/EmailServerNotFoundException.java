package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2019-12-12 10:20
 **/
public class EmailServerNotFoundException extends ApiRuntimeException {
	private static final long serialVersionUID = 3293831889717207715L;

	public EmailServerNotFoundException() {
		super("exception.framework.emailservernotfoundexception");
	}
}
