package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;

public class IntegrationSendRequestException extends ApiRuntimeException {

	private static final long serialVersionUID = 2626448627649753817L;

	public IntegrationSendRequestException(String uuid) {
		super("集成配置：'" + uuid + "'发送请求异常");
	}
}
