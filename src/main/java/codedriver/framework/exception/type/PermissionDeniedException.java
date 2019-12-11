package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class PermissionDeniedException extends ApiRuntimeException {

	public PermissionDeniedException() {
		super("没有权限进行当前操作");
	}
	

}
