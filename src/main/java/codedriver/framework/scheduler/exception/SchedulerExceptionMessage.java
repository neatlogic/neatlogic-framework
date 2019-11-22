package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.FrameworkExceptionMessageBase;

public class SchedulerExceptionMessage extends FrameworkExceptionMessageBase {

	private String message;
	
	public SchedulerExceptionMessage(String message) {
		this.message = message;
	}
	@Override
	public String getError() {
		return message;
	}

	@Override
	protected String myGetErrorCode() {
		return "08";
	}
	
	@Override
	public String toString() {
		return "Error:" + getErrorCode() + ", Message:" + message;
	}
}
