package codedriver.framework.exception.type;

import codedriver.framework.exception.core.IApiExceptionMessage;

public class PermissionDeniedExceptionMessage implements IApiExceptionMessage {

	@Override
	public String getErrorCode() {
		return "04";
	}

	@Override
	public String getError() {
		return "没有权限进行当前操作";
	}

}
