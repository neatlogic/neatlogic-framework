package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ApiMustBePublicException extends ApiRuntimeException {

    private static final long serialVersionUID = -925235736439720896L;

    public ApiMustBePublicException(String token) {
		super("不存在 '"+token+"' 的自定义接口");
	}

}
