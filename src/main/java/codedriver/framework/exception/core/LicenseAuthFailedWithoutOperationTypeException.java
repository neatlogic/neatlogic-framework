package codedriver.framework.exception.core;

public class LicenseAuthFailedWithoutOperationTypeException extends ApiRuntimeException {

    private static final long serialVersionUID = 4459732793577176306L;

    public LicenseAuthFailedWithoutOperationTypeException(String moduleGroup) {
        super("当前租户-'"+moduleGroup+"'模块无授权");
    }

}
