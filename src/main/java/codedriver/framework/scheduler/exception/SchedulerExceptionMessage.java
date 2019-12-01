package codedriver.framework.scheduler.exception;

import codedriver.framework.exception.core.IApiExceptionMessage;

public class SchedulerExceptionMessage implements IApiExceptionMessage {
	
	private String errorCode = "08";
	private String error = "定时作业功能-";
	
	public SchedulerExceptionMessage(IApiExceptionMessage message) {
		this.errorCode += message.getErrorCode();
		this.error += message.getError();
	}
	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getError() {
		return error;
	}
}
