package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class IntegrationNameRepeatsException extends ApiRuntimeException {

    private static final long serialVersionUID = 7995462613340939412L;

    public IntegrationNameRepeatsException(String name) {
        super("exception.framework.integrationnamerepeatsexception", name);
    }

}
