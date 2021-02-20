package codedriver.framework.exception.core;

public class ApiFieldValidRuntimeException extends Exception {

	private String errorCode;

	public ApiFieldValidRuntimeException() {
		super();
	}

	public ApiFieldValidRuntimeException(Throwable ex) {
		super(ex);
	}

	public ApiFieldValidRuntimeException(String message, Throwable ex) {
		super(message, ex);
	}

	public ApiFieldValidRuntimeException(String message) {
		super(message);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
