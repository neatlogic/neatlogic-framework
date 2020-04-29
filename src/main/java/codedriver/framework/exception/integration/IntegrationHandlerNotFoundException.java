package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;

public class IntegrationHandlerNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 4310317884995245784L;

	public IntegrationHandlerNotFoundException(String handler) {
		super("找不到集成配置处理器：" + handler);
	}
}
