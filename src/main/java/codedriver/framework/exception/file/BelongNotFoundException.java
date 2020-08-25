package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public  class BelongNotFoundException extends ApiRuntimeException {
	public BelongNotFoundException(String msg) {
		super(msg);
	}

}
