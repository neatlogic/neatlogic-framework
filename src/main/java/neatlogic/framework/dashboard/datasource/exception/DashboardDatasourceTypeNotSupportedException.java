package neatlogic.framework.dashboard.datasource.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 未注册的数据源类型必须明确拒绝，不能回退到数据仓库查询。 */
public class DashboardDatasourceTypeNotSupportedException extends ApiRuntimeException {
    private static final long serialVersionUID = 1L;

    /** 使用框架国际化资源保留既有不支持类型的业务错误文案。 */
    public DashboardDatasourceTypeNotSupportedException(String datasourceType) {
        super("nfdde.dashboarddatasourcetypenotsupportedexception.message", datasourceType);
    }
}
