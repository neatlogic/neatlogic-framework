package neatlogic.framework.dashboard.datasource.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 大屏集成规范或表头默认配置不完整时返回明确的业务错误。 */
public class DashboardIntegrationConfigInvalidException extends ApiRuntimeException {
    private static final long serialVersionUID = 1L;

    /** 原因由框架国际化资源提供，不使用无关矩阵功能文案。 */
    public DashboardIntegrationConfigInvalidException(String reason) {
        super("nfdde.dashboardintegrationconfiginvalidexception.message", reason);
    }
}
