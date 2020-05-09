package codedriver.framework.exception.auth;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NoAuthGroupException extends ApiRuntimeException {

	private static final long serialVersionUID = -804858290704711315L;

	public NoAuthGroupException(String authGroup) {
		super("不存在'"+authGroup+"'模块组");
	}
}
