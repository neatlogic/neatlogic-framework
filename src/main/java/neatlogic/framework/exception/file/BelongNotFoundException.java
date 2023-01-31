package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public  class BelongNotFoundException extends ApiRuntimeException {
	public BelongNotFoundException(String msg) {
		super(msg);
	}

}
