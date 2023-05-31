package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyNoReceiverException extends ApiRuntimeException {

	private static final long serialVersionUID = -6888897740938504421L;
	
	public NotifyNoReceiverException() {
		super("common.norecipient");
	}
}
