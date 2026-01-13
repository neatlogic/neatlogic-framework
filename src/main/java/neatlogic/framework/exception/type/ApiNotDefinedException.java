package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ApiNotDefinedException extends ApiRuntimeException {

    private static final long serialVersionUID = -925235736439720896L;

    public ApiNotDefinedException(String token) {
        super("“{0}”接口没有配置basic认证信息，请联系管理员配置", token);
    }

}
