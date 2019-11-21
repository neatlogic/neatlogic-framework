package codedriver.framework.exception;

public class AuthActionExceptionMessage extends FrameworkExceptionMessageBase {

	@Override
	protected String myGetErrorCode() {
		return "04";
	}

	@Override
	public String getError() {
		return "您没有权限进行当前操作";
	}

}
