package codedriver.framework.matrix.dao.mapper;

import codedriver.framework.matrix.dto.MatrixDataVo;

import java.util.List;
import java.util.Map;

public interface MatrixViewDataMapper {

    List<Map<String, Object>> getDynamicTableDataByUuidList(MatrixDataVo dataVo);

    int getDynamicTableDataCount(MatrixDataVo dataVo);

    List<Map<String, Object>> searchDynamicTableData(MatrixDataVo dataVo);

    int getDynamicTableDataCountForTable(MatrixDataVo dataVo);

    List<Map<String, Object>> getDynamicTableDataForTable(MatrixDataVo dataVo);

    int getDynamicTableDataCountForSelect(MatrixDataVo dataVo);

    List<Map<String, Object>> getDynamicTableDataForSelect(MatrixDataVo dataVo);

}
