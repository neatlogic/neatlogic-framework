package codedriver.framework.exception.type;

import codedriver.framework.exception.core.IApiExceptionMessage;

public class CustomExceptionMessage implements IApiExceptionMessage {
	private String error;

	public CustomExceptionMessage(String _error) {
		error = _error;
	}

	@Override
	public String getError() {
		return error;
	}

	@Override
	public String getErrorCode() {
		return "99";
	}

}
