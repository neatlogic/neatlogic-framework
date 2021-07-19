/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.dao.mapper;

import codedriver.framework.matrix.dto.MatrixViewVo;

/**
 * @author linbq
 * @since 2021/7/19 12:12
 **/
public interface MatrixViewMapper {

    public void insertMatrixView(MatrixViewVo matrixViewVo);

    public void updateMatrixView(MatrixViewVo matrixViewVo);

    public void deleteMatrixViewByMatrixUuid(String matrixUuid);

    public MatrixViewVo getMatrixViewByMatrixUuid(String matrixUuid);

    public int getMatrixViewIsExists(String matrixUuid);
}
