package codedriver.framework.exception.database;

import codedriver.framework.exception.core.ApiRuntimeException;

public class TableIsExistsException extends ApiRuntimeException {


    public TableIsExistsException(String tableName) {
        super("表：" + tableName + "已存在");
    }
}
