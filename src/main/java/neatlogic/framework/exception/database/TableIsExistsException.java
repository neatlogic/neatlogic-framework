package neatlogic.framework.exception.database;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class TableIsExistsException extends ApiRuntimeException {


    public TableIsExistsException(String tableName) {
        super("表：{0}已存在", tableName);
    }
}
