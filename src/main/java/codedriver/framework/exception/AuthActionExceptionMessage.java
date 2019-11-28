package codedriver.framework.exception;

public class AuthActionExceptionMessage implements IApiExceptionMessage {

	@Override
	public String getErrorCode() {
		return "04";
	}

	@Override
	public String getError() {
		return "您没有权限进行当前操作";
	}

}
