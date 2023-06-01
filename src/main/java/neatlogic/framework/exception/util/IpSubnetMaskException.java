package neatlogic.framework.exception.util;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class IpSubnetMaskException extends ApiRuntimeException {

    private static final long serialVersionUID = 8573298340823651568L;

    public IpSubnetMaskException(String param) {
        super("请检查ip网端/子网掩码配置后，重试:{0}", param);
    }
}
