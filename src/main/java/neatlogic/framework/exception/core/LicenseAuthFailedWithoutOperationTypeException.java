package neatlogic.framework.exception.core;

public class LicenseAuthFailedWithoutOperationTypeException extends ApiRuntimeException {

    private static final long serialVersionUID = 4459732793577176306L;

    public LicenseAuthFailedWithoutOperationTypeException(String moduleGroup,String operationType) {
        super("当前租户没有'"+moduleGroup+"-"+operationType+"'操作权限");
    }

}
