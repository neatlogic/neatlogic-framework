/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-09 11:03
 **/
public class MatrixDataSourceHandlerNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508275752609703532L;

    public MatrixDataSourceHandlerNotFoundException(String name) {
        super("矩阵数据源处理器：'" + name + "'不存在");
    }
}
