package codedriver.framework.exception;

import codedriver.framework.exception.IApiExceptionMessage;

public abstract class FrameworkExceptionMessageBase implements IApiExceptionMessage {
	@Override
	public final String getErrorCode() {
		return "01" + this.myGetErrorCode();
	}

	protected abstract String myGetErrorCode();


}
