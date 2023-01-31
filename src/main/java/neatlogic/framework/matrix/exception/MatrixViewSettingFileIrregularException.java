/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixViewSettingFileIrregularException extends ApiRuntimeException {
    public MatrixViewSettingFileIrregularException(Exception ex) {
        super("矩阵视图配置文件内容不合法：" + ex.getMessage());
    }

    public MatrixViewSettingFileIrregularException(String nodeName) {
        super("矩阵视图配置文件缺少节点：" + nodeName);
    }

    public MatrixViewSettingFileIrregularException(String nodeName, String attrName) {
        super("矩阵视图配置文件" + nodeName + "节点缺少属性" + attrName);
    }
}
