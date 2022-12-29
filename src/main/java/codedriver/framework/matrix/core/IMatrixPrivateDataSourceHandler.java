/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.core;

import codedriver.framework.matrix.dto.MatrixAttributeVo;
import codedriver.framework.matrix.dto.MatrixDataVo;

import java.util.List;
import java.util.Map;

/**
 * 矩阵私有类型数据源接口
 */
public interface IMatrixPrivateDataSourceHandler {

    String getUuid();

    String getName();

    String getLabel();

    default List<MatrixAttributeVo> getAttributeList() {
        return null;
    }

    default List<Map<String, String>> searchTableData(MatrixDataVo dataVo) {
        return null;
    }
}
