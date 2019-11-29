package codedriver.framework.exception;

import codedriver.framework.common.apiparam.ApiParamType;

public class AuthParamsExceptionMessage implements IApiExceptionMessage {
	private String paramName ="";
	private ApiParamType paramType = null;
	
	public AuthParamsExceptionMessage(String _paramName) {
		this.paramName = _paramName;
	}
	
	public AuthParamsExceptionMessage(String _paramName,ApiParamType _paramType) {
		this.paramName = _paramName;
		this.paramType = _paramType;
	}
	
	@Override
	public String getErrorCode() {
		return "05";
	}

	@Override
	public String getError() {
		if(null != this.paramType) {
			return "参数 '"+paramName+"' 类型异常,要求类型: "+paramType.getText();
		}else {
			return "参数 '"+paramName+"' 必填";
		}
	}

}
