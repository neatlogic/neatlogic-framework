package codedriver.framework.exception.core;

import codedriver.framework.exception.core.IApiExceptionMessage;

public  class FrameworkExceptionMessageBase implements IApiExceptionMessage {
	private String errorCode = "01";
	private String error = "基础模块-";
	
	public FrameworkExceptionMessageBase(IApiExceptionMessage function){
		this.errorCode += function.getErrorCode();
		this.error += function.getError();
	}
	
	@Override
	public final String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getError() {
		return error;
	}

}
