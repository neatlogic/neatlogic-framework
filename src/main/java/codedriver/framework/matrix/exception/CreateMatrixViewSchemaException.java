/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class CreateMatrixViewSchemaException extends ApiRuntimeException {
    public CreateMatrixViewSchemaException(String ciName) {
        super("创建矩阵：" + ciName + "数据表失败。");
    }

    public CreateMatrixViewSchemaException(String ciName, boolean isView) {
        super("创建矩阵：" + ciName + "数据视图失败。");
    }
}
