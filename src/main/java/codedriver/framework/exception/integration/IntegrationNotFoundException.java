package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;

public class IntegrationNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 1061691150909475176L;

	public IntegrationNotFoundException(String uuid) {
		super("找不到集成配置：" + uuid);
	}
}
