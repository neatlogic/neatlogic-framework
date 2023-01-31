/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixViewSqlIrregularException extends ApiRuntimeException {
    public MatrixViewSqlIrregularException(Exception msg) {
        super("矩阵视图SQL语句不是合法的SELECT语句：" + msg.getMessage());
    }

    public MatrixViewSqlIrregularException() {
        super("配置文件不是合法的XML文件");
    }
}
