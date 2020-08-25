package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ParamTypeNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 1061423112349475176L;

	public ParamTypeNotFoundException(String type) {
		super("参数类型：" + type + "不存在");
	}
}
