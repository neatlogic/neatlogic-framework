package codedriver.framework.exception.core;

public class LicenseAuthFailedWithoutModuleGroupException extends ApiRuntimeException {

    private static final long serialVersionUID = 4459732793577176306L;

    public LicenseAuthFailedWithoutModuleGroupException(String moduleGroup) {
        super("当前租户没有'"+moduleGroup+"'模块权限");
    }

}
