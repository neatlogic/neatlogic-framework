package neatlogic.framework.dashboard.datasource.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 大屏集成返回字段缺失或类型错误时，指出不符合规范的字段位置。 */
public class DashboardIntegrationResultInvalidException extends ApiRuntimeException {
    private static final long serialVersionUID = 1L;

    /** 字段路径保留原标识，错误说明按请求语言展示。 */
    public DashboardIntegrationResultInvalidException(String field) {
        super("nfdde.dashboardintegrationresultinvalidexception.message", field);
    }
}
