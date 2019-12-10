package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class AnonymousExceptionMessage extends ApiRuntimeException {

	public AnonymousExceptionMessage() {
		super("不允许匿名访问");
	}

}
