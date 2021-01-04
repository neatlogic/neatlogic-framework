package codedriver.framework.exception.olap;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.exception.core.ApiRuntimeException;

public class OlapDataBaseNotFoundExceptionException extends ApiRuntimeException {


    public OlapDataBaseNotFoundExceptionException() {
        super("租户：" + TenantContext.get().getTenantUuid() + "缺少OLAP数据库，请补充创建。");
    }
}
