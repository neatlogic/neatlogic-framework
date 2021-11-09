/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.core;

import codedriver.framework.matrix.dto.MatrixAttributeVo;
import codedriver.framework.matrix.dto.MatrixDataVo;
import codedriver.framework.matrix.dto.MatrixVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author linbq
 * @since 2021/11/4 17:18
 **/
public interface IMatrixDataSourceHandler {

    String getHandler();

    MatrixVo saveMatrix(MatrixVo matrixVo) throws Exception;

    MatrixVo getMatrix(String Uuid);

    void deleteMatrix(String uuid);

    void copyMatrix(String sourceUuid, MatrixVo matrixVo);

    JSONObject importMatrix(MatrixVo matrixVo, MultipartFile multipartFile) throws IOException;

    HSSFWorkbook exportMatrix(MatrixVo matrixVo);

    void saveAttributeList(String matrixUuid, List<MatrixAttributeVo> matrixAttributeList);

    List<MatrixAttributeVo> getAttributeList(MatrixVo matrixVo);

    JSONObject exportAttribute(MatrixVo matrixVo);

    JSONObject getTableData(MatrixDataVo dataVo);

    JSONObject TableDataSearch(MatrixDataVo dataVo);

    List<Map<String, JSONObject>> TableColumnDataSearch(MatrixDataVo dataVo);

    void saveTableRowData(String matrixUuid, JSONObject rowData);

    Map<String, String> getTableRowData(MatrixDataVo matrixDataVo);

    void deleteTableRowData(String matrixUuid, List<String> uuidList);
}
