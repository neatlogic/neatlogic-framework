package neatlogic.framework.matrix.dao.mapper;

import neatlogic.framework.matrix.dto.MatrixColumnVo;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MatrixDataMapper {

    int getDynamicTableDataCountByUuid(MatrixDataVo dataVo);

    Map<String, String> getDynamicRowDataByUuid(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataByUuidList(MatrixDataVo dataVo);

    int getDynamicTableDataCount(MatrixDataVo dataVo);

    List<Map<String, String>> searchDynamicTableData(MatrixDataVo dataVo);

    int getDynamicTableDataCountForTable(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataForTable(MatrixDataVo dataVo);

    int getDynamicTableDataCountForSelect(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataForSelect(MatrixDataVo dataVo);

    int getDynamicTableDataListCount(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataList(MatrixDataVo dataVo);

    Map<String, Long> checkMatrixAttributeHasDataByAttributeUuidList(@Param("matrixUuid") String matrixUuid, @Param("attributeUuidList") List<String> attributeUuidList);

    Integer getMaxSort(String matrixUuid);

    Integer getSortByUuid(@Param("uuid") String uuid, @Param("matrixUuid") String matrixUuid);

    int insertDynamicTableData(@Param("rowData") List<MatrixColumnVo> rowData, @Param("matrixUuid") String matrixUuid);

    int insertDynamicTableDataForCopy(
            @Param("sourceMatrixUuid") String sourceMatrixUuid,
            @Param("sourceColumnList") List<String> sourceColumnList,
            @Param("targetMatrixUuid") String targetMatrixUuid,
            @Param("targetColumnList") List<String> targetColumnList
    );

    int updateDynamicTableDataByUuid(@Param("rowData") List<MatrixColumnVo> rowData, @Param("uuid") String uuid, @Param("matrixUuid") String matrixUuid);

    int updateSortByUuid(@Param("uuid") String uuid, @Param("matrixUuid") String matrixUuid, @Param("sort") Integer sort);

    int updateSortIncrement(@Param("matrixUuid") String matrixUuid,
                            @Param("fromSort") Integer fromSort,
                            @Param("toSort") Integer toSort);

    int updateSortDecrement(@Param("matrixUuid") String matrixUuid,
                            @Param("fromSort") Integer fromSort,
                            @Param("toSort") Integer toSort);

    void batchUpdateSortequalsId(String matrixUuid);

    int deleteDynamicTableDataByUuid(MatrixDataVo dataVo);
}
