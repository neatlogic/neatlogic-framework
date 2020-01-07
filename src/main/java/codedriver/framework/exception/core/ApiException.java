package codedriver.framework.exception.core;

public class ApiException extends Exception {
	private String errorCode;

	public ApiException() {
		super();
	}

	public ApiException(Throwable ex) {
		super(ex);
	}

	public ApiException(String message, Throwable ex) {
		super(message, ex);
	}

	public ApiException(String message) {
		super(message);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
