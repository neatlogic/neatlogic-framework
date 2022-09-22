package codedriver.framework.matrix.dao.mapper;

import codedriver.framework.matrix.dto.MatrixAttributeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MatrixAttributeMapper {

    List<MatrixAttributeVo> getMatrixAttributeByMatrixUuid(String matrixUuid);

    int insertMatrixAttribute(MatrixAttributeVo matrixAttributeVo);

    void deleteAttributeByMatrixUuid(String matrixUuid);

    void createMatrixDynamicTable(@Param("attributeList") List<MatrixAttributeVo> attributeList, @Param("matrixUuid") String matrixUuid);

    void dropMatrixDynamicTable(String matrixUuid);

    void addMatrixDynamicTableColumn(@Param("columnName") String columnName, @Param("matrixUuid") String matrixUuid);

    void dropMatrixDynamicTableColumn(@Param("columnName") String columnName, @Param("matrixUuid") String matrixUuid);
}
