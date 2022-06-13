package codedriver.framework.exception.core;

public class LicenseInvalidException extends ApiRuntimeException {

    private static final long serialVersionUID = -5752556137587998073L;

    public LicenseInvalidException() {
        super("当前租户没有license授权或license无效");
    }

    public LicenseInvalidException(String tenant) {
        super("当前租户没有license授权");
    }

}
