package codedriver.framework.exception.core;

public class NoLicenseException extends ApiRuntimeException {

    private static final long serialVersionUID = -5752556137587998073L;

    public NoLicenseException() {
        super("当前租户没有license授权");
    }

}
