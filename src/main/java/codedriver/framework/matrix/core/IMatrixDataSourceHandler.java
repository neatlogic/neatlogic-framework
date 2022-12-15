/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.core;

import codedriver.framework.matrix.dto.MatrixAttributeVo;
import codedriver.framework.matrix.dto.MatrixDataVo;
import codedriver.framework.matrix.dto.MatrixVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author linbq
 * @since 2021/11/4 17:18
 **/
public interface IMatrixDataSourceHandler {

    String getHandler();

    /**
     * 保存矩阵信息
     *
     * @param matrixVo
     * @return
     * @throws Exception
     */
    void saveMatrix(MatrixVo matrixVo) throws Exception;

    /**
     * 查询矩阵信息
     *
     * @param matrixVo
     * @return
     */
    MatrixVo getMatrix(MatrixVo matrixVo);

    /**
     * 删除矩阵信息
     *
     * @param uuid
     */
    void deleteMatrix(String uuid);

    /**
     * 复制矩阵
     *
     * @param sourceUuid
     * @param matrixVo
     */
    void copyMatrix(String sourceUuid, MatrixVo matrixVo);

    /**
     * 导入矩阵
     *
     * @param matrixVo
     * @param multipartFile
     * @return
     * @throws IOException
     */
    JSONObject importMatrix(MatrixVo matrixVo, MultipartFile multipartFile) throws IOException;

    /**
     * 导出矩阵为CSV
     *
     * @param matrixVo
     * @param os
     * @throws IOException
     */
    void exportMatrix2CSV(MatrixVo matrixVo, OutputStream os) throws Exception;

    /**
     * 导出矩阵为EXCEL
     *
     * @param matrixVo
     * @return
     */
    Workbook exportMatrix2Excel(MatrixVo matrixVo);

    /**
     * 保存矩阵属性列表信息
     *
     * @param matrixUuid
     * @param matrixAttributeList
     */
    void saveAttributeList(String matrixUuid, List<MatrixAttributeVo> matrixAttributeList);

    /**
     * 查询矩阵属性列表信息
     *
     * @param matrixVo
     * @return
     */
    List<MatrixAttributeVo> getAttributeList(MatrixVo matrixVo);

    /**
     * 导出模板
     *
     * @param matrixVo
     * @return
     */
    JSONObject exportAttribute(MatrixVo matrixVo);

    /**
     * 查询矩阵表格数据
     *
     * @param dataVo
     * @return
     */
    JSONObject getTableData(MatrixDataVo dataVo);

    /**
     * 查询矩阵表格数据
     *
     * @param dataVo
     * @return
     */
    JSONObject searchTableData(MatrixDataVo dataVo);

    /**
     * 查询矩阵表格某列数据
     *
     * @param dataVo
     * @return
     */
    List<Map<String, JSONObject>> searchTableColumnData(MatrixDataVo dataVo);

    /**
     * 查询矩阵表格某列数据
     *
     * @param dataVo
     * @return
     */
    List<Map<String, JSONObject>> searchTableDataNew(MatrixDataVo dataVo);

    /**
     * 保存矩阵表格一行数据
     *
     * @param matrixUuid
     * @param rowData
     */
    void saveTableRowData(String matrixUuid, JSONObject rowData);

    /**
     * 查询矩阵表格一行数据
     *
     * @param matrixDataVo
     * @return
     */
    Map<String, String> getTableRowData(MatrixDataVo matrixDataVo);

    /**
     * 删除矩阵表格多行数据
     *
     * @param matrixUuid
     * @param uuidList
     */
    void deleteTableRowData(String matrixUuid, List<String> uuidList);
}
