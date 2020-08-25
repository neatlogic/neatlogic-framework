package codedriver.framework.exception.core;

public class ApiRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 9206337410118158624L;
	private String errorCode;

	public ApiRuntimeException() {
		super();
	}

	public ApiRuntimeException(Throwable ex) {
		super(ex);
	}

	public ApiRuntimeException(String message, Throwable ex) {
		super(message, ex);
	}

	public ApiRuntimeException(String message) {
		super(message);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
