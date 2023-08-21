package neatlogic.framework.exception.sql;

import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.exception.core.ApiRuntimeException;

public class DmlSqlExecuteException extends ApiRuntimeException {


    public DmlSqlExecuteException(String sql, TenantVo tenant, String moduleId) {
        super("nfes.dmlsqlexecuteexception.dmlsqlexecuteexception", tenant.getName(), moduleId, sql);
    }
}
