package neatlogic.framework.exception.database;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.exception.core.ApiRuntimeException;

public class DataBaseNotFoundException extends ApiRuntimeException {


    public DataBaseNotFoundException() {
        super("租户：{0}缺少DATA数据库，请补充创建。", TenantContext.get().getTenantUuid());
    }
}
