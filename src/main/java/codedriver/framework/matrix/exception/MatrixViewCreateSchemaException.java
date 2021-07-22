/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixViewCreateSchemaException extends ApiRuntimeException {
    public MatrixViewCreateSchemaException(String name) {
        super("创建矩阵：" + name + "数据表失败。");
    }

    public MatrixViewCreateSchemaException(String name, boolean isView) {
        super("创建矩阵：" + name + "数据视图失败。");
    }
}
