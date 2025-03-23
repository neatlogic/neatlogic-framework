package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class IntegrationNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1061691150909475176L;

    public IntegrationNotFoundException(String uuid) {
        super("集成“{0}”不存在", uuid);
    }

    public IntegrationNotFoundException(List<String> uuidList) {
        super("集成“{0}”不存在", StringUtils.join(uuidList));
    }
}
