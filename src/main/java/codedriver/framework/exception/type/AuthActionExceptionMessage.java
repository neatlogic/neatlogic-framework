package codedriver.framework.exception.type;

import codedriver.framework.exception.core.IApiExceptionMessage;

public class AuthActionExceptionMessage implements IApiExceptionMessage {
	private String actionMsg ="";
	public AuthActionExceptionMessage(String _actionMsg) {
		this.actionMsg = _actionMsg;
	}
	
	@Override
	public String getErrorCode() {
		return "04";
	}

	@Override
	public String getError() {
		return "您没有权限进行当前 '"+actionMsg+"' 操作";
	}

}
