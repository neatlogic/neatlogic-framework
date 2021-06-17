package codedriver.framework.exception.core;

import com.alibaba.fastjson.JSONObject;

public class ApiRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 9206337410118158624L;
	private String errorCode;

	private JSONObject param;

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

	public ApiRuntimeException(String message, JSONObject param) {
		super(message);
		this.param = param;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public JSONObject getParam() {
		return param;
	}

}
