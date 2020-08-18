package codedriver.framework.exception.solution;

import codedriver.framework.exception.core.ApiRuntimeException;

public class SolutionNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -3755737935060105949L;

	public SolutionNotFoundException(Long id) {
		super("解决方案：" + id + "不存在");
	}


}
