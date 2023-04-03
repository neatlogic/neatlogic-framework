package neatlogic.framework.exception.database;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.exception.core.ApiRuntimeException;

public class DataBaseNotFoundException extends ApiRuntimeException {


    public DataBaseNotFoundException() {
        super("exception.framework.databasenotfoundexception", TenantContext.get().getTenantUuid());
    }
}
