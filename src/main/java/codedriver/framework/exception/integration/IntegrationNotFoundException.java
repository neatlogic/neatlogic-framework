package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class IntegrationNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1061691150909475176L;

    public IntegrationNotFoundException(String uuid) {
        super("找不到集成配置：" + uuid);
    }

    public IntegrationNotFoundException(List<String> uuidList) {
        super("找不到集成配置：" + StringUtils.join(uuidList));
    }
}
