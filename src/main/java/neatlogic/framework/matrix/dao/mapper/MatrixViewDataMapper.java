package neatlogic.framework.matrix.dao.mapper;

import neatlogic.framework.matrix.dto.MatrixDataVo;

import java.util.List;
import java.util.Map;

public interface MatrixViewDataMapper {

    List<Map<String, String>> getDynamicTableDataByUuidList(MatrixDataVo dataVo);

    int getDynamicTableDataCount(MatrixDataVo dataVo);

    List<Map<String, String>> searchDynamicTableData(MatrixDataVo dataVo);

    int getDynamicTableDataCountForTable(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataForTable(MatrixDataVo dataVo);

    int getDynamicTableDataCountForSelect(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataForSelect(MatrixDataVo dataVo);

    int getDynamicTableDataListCount(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataList(MatrixDataVo dataVo);
}
