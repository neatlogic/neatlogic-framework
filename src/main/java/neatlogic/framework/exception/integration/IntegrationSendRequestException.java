package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class IntegrationSendRequestException extends ApiRuntimeException {

    private static final long serialVersionUID = 2626448627649753817L;

    public IntegrationSendRequestException(String uuid) {
        super("exception.framework.integrationsendrequestexception", uuid);
    }
}
