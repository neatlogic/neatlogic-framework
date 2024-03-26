package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixCmdbCustomViewNotFoundException extends ApiRuntimeException {

    public MatrixCmdbCustomViewNotFoundException(String name) {
        super("矩阵：{0}的自定义视图配置信息不存在", name);
    }
}
