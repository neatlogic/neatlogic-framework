package codedriver.framework.matrix.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.matrix.dto.MatrixAttributeVo;

public interface MatrixAttributeMapper {
    public int insertMatrixAttribute(MatrixAttributeVo matrixAttributeVo);

    public List<MatrixAttributeVo> getMatrixAttributeByMatrixUuid(String matrixUuid);

    public void deleteAttributeByMatrixUuid(String matrixUuid);

    public int checkMatrixAttributeTableExist(String tableName);

    public void createMatrixDynamicTable(@Param("attributeList") List<MatrixAttributeVo> attributeList,@Param("matrixUuid") String matrixUuid);

    public void dropMatrixDynamicTable(String tableName);

    public void addMatrixDynamicTableColumn(@Param("columnName") String columnName,@Param("matrixUuid") String matrixUuid);

    public void dropMatrixDynamicTableColumn(@Param("columnName") String columnName,@Param("matrixUuid") String matrixUuid);
}
