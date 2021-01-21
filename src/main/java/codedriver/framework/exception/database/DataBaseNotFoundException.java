package codedriver.framework.exception.database;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.exception.core.ApiRuntimeException;

public class DataBaseNotFoundException extends ApiRuntimeException {


    public DataBaseNotFoundException() {
        super("租户：" + TenantContext.get().getTenantUuid() + "缺少DATA数据库，请补充创建。");
    }
}
