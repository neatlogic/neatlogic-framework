package codedriver.framework.exception;

public class AnonymousExceptionMessage implements IApiExceptionMessage {

	@Override
	public String getErrorCode() {
		return "01";
	}

	@Override
	public String getError() {
		return "不允许匿名访问";
	}

}
