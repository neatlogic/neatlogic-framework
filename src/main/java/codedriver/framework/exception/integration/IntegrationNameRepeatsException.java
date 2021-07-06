package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;

public class IntegrationNameRepeatsException extends ApiRuntimeException {

    private static final long serialVersionUID = 7995462613340939412L;

    public IntegrationNameRepeatsException(String name) {
        super("集成配置：'" + name + "'已存在");
    }

}
