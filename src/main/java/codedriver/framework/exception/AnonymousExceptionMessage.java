package codedriver.framework.exception;

public class AnonymousExceptionMessage extends FrameworkExceptionMessageBase {

	@Override
	protected String myGetErrorCode() {
		return "01";
	}

	@Override
	public String getError() {
		return "不允许匿名访问";
	}

}
