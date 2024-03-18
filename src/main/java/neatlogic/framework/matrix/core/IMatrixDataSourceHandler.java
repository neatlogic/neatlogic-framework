/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.matrix.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import neatlogic.framework.matrix.dto.MatrixVo;
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
     * 导出矩阵的定义及数据
     * @param matrixVo
     * @return
     */
    MatrixVo exportMatrix(MatrixVo matrixVo);
    /**
     * 导入矩阵的定义及数据
     * @param matrixVo
     * @return
     */
    void importMatrix(MatrixVo matrixVo);

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
    JSONObject searchTableData(MatrixDataVo dataVo);

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

    /**
     * 移动行数据顺序
     * @param matrixUuid 矩阵唯一标识
     * @param uuid 被移动行数据唯一标识
     * @param toUuid 移动后，被移动行占据的位置之前的行数据唯一标识
     */
    void moveTableRowDataSort(String matrixUuid, String uuid, String toUuid);

}
