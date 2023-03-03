package neatlogic.framework.exception.core;

import org.apache.commons.lang3.StringUtils;

public class LicenseInvalidException extends ApiRuntimeException {

    private static final long serialVersionUID = -5752556137587998073L;

    public LicenseInvalidException() {
        super("当前租户没有license授权或license无效");
    }

    public LicenseInvalidException(String msg) {
        super("当前租户没有license授权: " + msg);
    }

    public LicenseInvalidException(String tenantUuid, String msg, String licenseStr) {
        super(tenantUuid + " : " + msg + (StringUtils.isNotBlank(licenseStr) ? " : " : StringUtils.EMPTY) + licenseStr);
    }

}
