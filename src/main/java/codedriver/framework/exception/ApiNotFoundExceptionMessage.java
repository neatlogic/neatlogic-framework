package codedriver.framework.exception;

public class ApiNotFoundExceptionMessage implements IApiExceptionMessage {
	private String token;

	public ApiNotFoundExceptionMessage(String _token) {
		token = _token;
	}

	@Override
	public String getError() {
		return "token为：" + token + "的接口不存在或已被禁用";
	}

	@Override
	public String getErrorCode() {
		return "03";
	}

}
