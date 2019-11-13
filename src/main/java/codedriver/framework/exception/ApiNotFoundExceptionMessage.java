package codedriver.framework.exception;

public class ApiNotFoundExceptionMessage extends FrameworkExceptionMessageBase {
	private String token;

	public ApiNotFoundExceptionMessage(String _token) {
		token = _token;
	}

	@Override
	public String getError() {
		return "token为：" + token + "的接口不存在或已被禁用";
	}

	@Override
	protected String myGetErrorCode() {
		return "03";
	}

}
