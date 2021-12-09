package codedriver.framework.matrix.dao.mapper;

import codedriver.framework.matrix.dto.MatrixColumnVo;
import codedriver.framework.matrix.dto.MatrixDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MatrixViewDataMapper {

    List<Map<String, String>> getDynamicTableDataByUuidList(MatrixDataVo dataVo);

    int getDynamicTableDataCount(MatrixDataVo dataVo);

    List<Map<String, String>> searchDynamicTableData(MatrixDataVo dataVo);

    int getDynamicTableDataCountForTable(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataForTable(MatrixDataVo dataVo);

    List<Map<String, String>> getDynamicTableDataForSelect(MatrixDataVo dataVo);

}
