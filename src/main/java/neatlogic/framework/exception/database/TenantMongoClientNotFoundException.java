package neatlogic.framework.exception.database;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.exception.core.ApiRuntimeException;

import java.io.Serial;

public class TenantMongoClientNotFoundException extends ApiRuntimeException {

    @Serial
    private static final long serialVersionUID = -4991230298560307983L;

    public TenantMongoClientNotFoundException() {
        super("nfed.tenantmongoclientnotfoundexception.tenantmongoclientnotfoundexception", TenantContext.get().getTenantUuid());
    }
}
