package codedriver.framework.matrix.dao.mapper;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.matrix.dto.MatrixColumnVo;
import codedriver.framework.matrix.dto.MatrixDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MatrixDataMapper {
    public int insertDynamicTableData(@Param("rowData") List<MatrixColumnVo> rowData, @Param("matrixUuid") String matrixUuid, @Param("tenantId") String tenantId);

    public int updateDynamicTableDataByUuid(@Param("rowData") List<MatrixColumnVo> rowData, @Param("uuidColumn") MatrixColumnVo uuidColumn, @Param("matrixUuid") String matrixUuid, @Param("tenantId") String tenantId);

    public int deleteDynamicTableDataByUuid(@Param("matrixUuid") String matrixUuid, @Param("uuid") String uuid, @Param("tenantId") String tenantId);

    public int getDynamicTableDataCount(@Param("dataVo") MatrixDataVo dataVo, @Param("tenantId") String tenantId);

    public int getDynamicTableDataCountByUuid(@Param("uuid") String uuid, @Param("matrixUuid") String matrixUuid, @Param("tenantId") String tenantId);

    public List<Map<String, String>> searchDynamicTableData(@Param("dataVo") MatrixDataVo dataVo, @Param("tenantId") String tenantId);

    public List<Map<String, String>> getDynamicTableDataByColumnList(@Param("dataVo") MatrixDataVo dataVo, @Param("tenantId") String tenantId);

    public List<Map<String, String>> getDynamicTableDataByUuidList(@Param("dataVo") MatrixDataVo processMatrixDataVo, @Param("tenantId") String tenantId);

    public int getDynamicTableDataByColumnCount(@Param("dataVo") MatrixDataVo dataVo, @Param("tenantId") String tenantId);

    public int getDynamicTableDataByUuidCount(@Param("dataVo") MatrixDataVo dataVo, @Param("tenantId") String tenantId);

    public List<String> getDynamicTableCellData(@Param("matrixUuid") String matrixUuid, @Param("sourceColumnVo") MatrixColumnVo sourceColumnVo, @Param("targetColumn") String targetColumn, @Param("tenantId") String tenantId);

    public List<ValueTextVo> getDynamicTableCellDataMap(@Param("matrixUuid") String matrixUuid, @Param("sourceColumn") String sourceColumn, @Param("targetColumn") String targetColumn, @Param("list") List<String> values, @Param("tenantId") String tenantId);

    public List<Map<String, String>> getDynamicTableDataByColumnList2(@Param("dataVo") MatrixDataVo dataVo, @Param("tenantId") String tenantId);

    public Map<String, String> getDynamicRowDataByUuid(@Param("dataVo") MatrixDataVo dataVo, @Param("tenantId") String tenantId);

    public Map<String, Long> checkMatrixAttributeHasDataByAttributeUuidList(@Param("matrixUuid") String matrixUuid, @Param("attributeUuidList") List<String> attributeUuidList, @Param("tenantId") String tenantId);

    public int insertDynamicTableDataForCopy(
            @Param("sourceMatrixUuid") String sourceMatrixUuid,
            @Param("sourceColumnList") List<String> sourceColumnList,
            @Param("targetMatrixUuid") String targetMatrixUuid,
            @Param("targetColumnList") List<String> targetColumnList,
            @Param("tenantId") String tenantId
    );
}
