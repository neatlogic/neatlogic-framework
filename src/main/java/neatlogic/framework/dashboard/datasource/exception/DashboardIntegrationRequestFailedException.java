package neatlogic.framework.dashboard.datasource.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 集成执行或转换失败时保留实际错误，不将失败响应当作空数据。 */
public class DashboardIntegrationRequestFailedException extends ApiRuntimeException {
    private static final long serialVersionUID = 1L;

    /** 原始错误作为参数传递，调用方可获得具体失败原因。 */
    public DashboardIntegrationRequestFailedException(String error) {
        super("nfdde.dashboardintegrationrequestfailedexception.message", error);
    }
}
