package codedriver.framework.matrix.dao.mapper;

import codedriver.framework.matrix.dto.MatrixAttributeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MatrixAttributeMapper {
    public int insertMatrixAttribute(MatrixAttributeVo matrixAttributeVo);

    public List<MatrixAttributeVo> getMatrixAttributeByMatrixUuid(String matrixUuid);

    public void deleteAttributeByMatrixUuid(String matrixUuid);

//    public int checkMatrixAttributeTableExist(String tableName);

    public void createMatrixDynamicTable(@Param("attributeList") List<MatrixAttributeVo> attributeList, @Param("matrixUuid") String matrixUuid, @Param("tenantId") String tenantId);

    public void dropMatrixDynamicTable(@Param("matrixUuid") String matrixUuid, @Param("tenantId") String tenantId);

    public void addMatrixDynamicTableColumn(@Param("columnName") String columnName, @Param("matrixUuid") String matrixUuid, @Param("tenantId") String tenantId);

    public void dropMatrixDynamicTableColumn(@Param("columnName") String columnName, @Param("matrixUuid") String matrixUuid, @Param("tenantId") String tenantId);
}
