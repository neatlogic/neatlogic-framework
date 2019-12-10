package codedriver.framework.exception.core;

public class CustomException extends RuntimeException {
	private static final long serialVersionUID = 9206337410118158624L;
	private String errorCode;

	public CustomException() {
		super();
	}

	public CustomException(Throwable ex) {
		super(ex);
	}

	public CustomException(String message, Throwable ex) {
		super(message, ex);
	}

	public CustomException(String message) {
		super(message);
	}

	public CustomException(IApiExceptionMessage exception) {
		super(exception.getError());
		this.errorCode = exception.getErrorCode();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
