package codedriver.framework.exception;

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
		return "06";
	}

}
