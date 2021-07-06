package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class HttpMethodNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -5654265221080809719L;

    public HttpMethodNotFoundException(String method) {
        super("不存在的请求方式：" + method);
    }

}
