package codedriver.framework.matrix.dao.mapper;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.matrix.dto.MatrixColumnVo;
import codedriver.framework.matrix.dto.MatrixDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MatrixDataMapper {
    int insertDynamicTableData(@Param("rowData") List<MatrixColumnVo> rowData, @Param("matrixUuid") String matrixUuid, @Param("schemaName") String schemaName);

    int updateDynamicTableDataByUuid(@Param("rowData") List<MatrixColumnVo> rowData, @Param("uuidColumn") MatrixColumnVo uuidColumn, @Param("matrixUuid") String matrixUuid, @Param("schemaName") String schemaName);

    int deleteDynamicTableDataByUuid(@Param("matrixUuid") String matrixUuid, @Param("uuid") String uuid, @Param("schemaName") String schemaName);

    int getDynamicTableDataCount(MatrixDataVo dataVo);

    int getDynamicTableDataCountByUuid(@Param("uuid") String uuid, @Param("matrixUuid") String matrixUuid, @Param("schemaName") String schemaName);

    List<Map<String, String>> searchDynamicTableData(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataByColumnList(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataByUuidList(MatrixDataVo dataVo);

    int getDynamicTableDataByColumnCount(MatrixDataVo dataVo);

    int getDynamicTableDataByUuidCount(MatrixDataVo dataVo);

    List<ValueTextVo> getDynamicTableCellDataMap(@Param("matrixUuid") String matrixUuid, @Param("sourceColumn") String sourceColumn, @Param("targetColumn") String targetColumn, @Param("list") List<String> values, @Param("schemaName") String schemaName);

    List<Map<String, String>> getDynamicTableDataByColumnList2(MatrixDataVo dataVo);

    Map<String, String> getDynamicRowDataByUuid(MatrixDataVo dataVo);

    Map<String, Long> checkMatrixAttributeHasDataByAttributeUuidList(@Param("matrixUuid") String matrixUuid, @Param("attributeUuidList") List<String> attributeUuidList, @Param("schemaName") String schemaName);

    int insertDynamicTableDataForCopy(
            @Param("sourceMatrixUuid") String sourceMatrixUuid,
            @Param("sourceColumnList") List<String> sourceColumnList,
            @Param("targetMatrixUuid") String targetMatrixUuid,
            @Param("targetColumnList") List<String> targetColumnList,
            @Param("schemaName") String schemaName
    );
}
