package codedriver.framework.exception;

public class AuthActionExceptionMessage extends FrameworkExceptionMessageBase {
	private String actionMsg ="";
	public AuthActionExceptionMessage(String _actionMsg) {
		this.actionMsg = _actionMsg;
	}
	
	@Override
	protected String myGetErrorCode() {
		return "04";
	}

	@Override
	public String getError() {
		return "您没有权限进行当前 '"+actionMsg+"' 操作";
	}

}
