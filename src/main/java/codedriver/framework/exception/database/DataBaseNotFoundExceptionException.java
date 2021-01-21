package codedriver.framework.exception.database;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.exception.core.ApiRuntimeException;

public class DataBaseNotFoundExceptionException extends ApiRuntimeException {


    public DataBaseNotFoundExceptionException() {
        super("租户：" + TenantContext.get().getTenantUuid() + "缺少DATA数据库，请补充创建。");
    }
}
