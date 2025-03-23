package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class IntegrationUnActiveException extends ApiRuntimeException {
    public IntegrationUnActiveException(String uuid) {
        super("集成“{0}”未激活", uuid);
    }

}
