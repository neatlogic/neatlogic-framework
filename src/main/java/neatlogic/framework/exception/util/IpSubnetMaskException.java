package neatlogic.framework.exception.util;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class IpSubnetMaskException extends ApiRuntimeException {

    private static final long serialVersionUID = 8573298340823651568L;

    public IpSubnetMaskException(String ip, String param) {
        super("nfeu.ipsubnetmaskexception.ipsubnetmaskexception", ip, param);
    }
}
