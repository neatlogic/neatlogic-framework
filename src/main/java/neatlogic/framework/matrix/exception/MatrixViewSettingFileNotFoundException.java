/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @author linbq
 * @since 2021/7/15 10:11
 **/
public class MatrixViewSettingFileNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -6114648438319986274L;

    public MatrixViewSettingFileNotFoundException() {
        super("矩阵视图配置文件不能为空");
    }
}
