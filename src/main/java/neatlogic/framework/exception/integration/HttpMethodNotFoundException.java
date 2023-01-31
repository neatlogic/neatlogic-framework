package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class HttpMethodNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -5654265221080809719L;

    public HttpMethodNotFoundException(String method) {
        super("不存在的请求方式：" + method);
    }

}
