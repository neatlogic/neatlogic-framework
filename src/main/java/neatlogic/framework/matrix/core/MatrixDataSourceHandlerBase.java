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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.ExportFileType;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.dependency.constvalue.FrameworkFromType;
import neatlogic.framework.dependency.core.DependencyManager;
import neatlogic.framework.exception.file.FileTypeNotSupportToExportException;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.matrix.dto.*;
import neatlogic.framework.matrix.exception.MatrixAttributeNotFoundException;
import neatlogic.framework.matrix.exception.MatrixReferencedCannotBeDeletedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linbq
 * @since 2021/11/5 10:26
 **/
public abstract class MatrixDataSourceHandlerBase implements IMatrixDataSourceHandler {

    protected static MatrixMapper matrixMapper;

    @Resource
    public void setMatrixMapper(MatrixMapper _matrixMapper) {
        matrixMapper = _matrixMapper;
    }

    @Override
    public void saveMatrix(MatrixVo matrixVo) throws Exception {
        if (matrixMapper.checkMatrixIsExists(matrixVo.getUuid()) == 0) {
            matrixMapper.insertMatrix(matrixVo);
            mySaveMatrix(matrixVo);
        } else {
            if (mySaveMatrix(matrixVo)) {
                matrixMapper.updateMatrixNameAndLcu(matrixVo);
            }
        }
    }

    protected abstract boolean mySaveMatrix(MatrixVo matrixVo) throws Exception;

    @Override
    public MatrixVo getMatrix(MatrixVo matrixVo) {
        myGetMatrix(matrixVo);
        return matrixVo;
    }

    protected abstract void myGetMatrix(MatrixVo matrixVo);

    @Override
    public void deleteMatrix(String uuid) {
        if (DependencyManager.getDependencyCount(FrameworkFromType.MATRIX, uuid) > 0) {
            throw new MatrixReferencedCannotBeDeletedException(uuid);
        }
        matrixMapper.deleteMatrixByUuid(uuid);
        myDeleteMatrix(uuid);
    }

    protected abstract void myDeleteMatrix(String uuid);

    @Override
    public void copyMatrix(String sourceUuid, MatrixVo matrixVo) {
        matrixVo.setFcu(UserContext.get().getUserUuid(true));
        matrixVo.setLcu(UserContext.get().getUserUuid(true));
        matrixMapper.insertMatrix(matrixVo);
        myCopyMatrix(sourceUuid, matrixVo);
    }

    protected abstract void myCopyMatrix(String sourceUuid, MatrixVo matrixVo);

    @Override
    public JSONObject importMatrix(MatrixVo matrixVo, MultipartFile multipartFile) throws IOException {
        return myImportMatrix(matrixVo, multipartFile);
    }

    protected abstract JSONObject myImportMatrix(MatrixVo matrixVo, MultipartFile multipartFile) throws IOException;

    @Override
    public void exportMatrix2CSV(MatrixVo matrixVo, OutputStream os) throws Exception {
        myExportMatrix2CSV(matrixVo, os);
    }

    @Override
    public Workbook exportMatrix2Excel(MatrixVo matrixVo) {
        return myExportMatrix2Excel(matrixVo);
    }

    protected void myExportMatrix2CSV(MatrixVo matrixVo, OutputStream os) throws Exception {
        throw new FileTypeNotSupportToExportException(ExportFileType.CSV.getValue());
    }

    protected Workbook myExportMatrix2Excel(MatrixVo matrixVo) {
        throw new FileTypeNotSupportToExportException(ExportFileType.EXCEL.getValue());
    }

    @Override
    public MatrixVo exportMatrix(MatrixVo matrixVo) {
        return myExportMatrix(matrixVo);
    }

    protected abstract MatrixVo myExportMatrix(MatrixVo matrixVo);

    @Override
    public void importMatrix(MatrixVo matrixVo) {
        if (matrixMapper.checkMatrixIsExists(matrixVo.getUuid()) > 0) {
            matrixMapper.deleteMatrixByUuid(matrixVo.getUuid());
        }
        matrixVo.setFcu(UserContext.get().getUserUuid());
        matrixVo.setLcu(UserContext.get().getUserUuid());
        matrixMapper.insertMatrix(matrixVo);
        myImportMatrix(matrixVo);
    }

    protected abstract void myImportMatrix(MatrixVo matrixVo);

    @Override
    public void saveAttributeList(String matrixUuid, List<MatrixAttributeVo> matrixAttributeList) {
        mySaveAttributeList(matrixUuid, matrixAttributeList);
    }

    protected abstract void mySaveAttributeList(String matrixUuid, List<MatrixAttributeVo> matrixAttributeList);

    @Override
    public List<MatrixAttributeVo> getAttributeList(MatrixVo matrixVo) {
        return myGetAttributeList(matrixVo);
    }

    protected abstract List<MatrixAttributeVo> myGetAttributeList(MatrixVo matrixVo);

    @Override
    public JSONObject exportAttribute(MatrixVo matrixVo) {
        return myExportAttribute(matrixVo);
    }

    protected abstract JSONObject myExportAttribute(MatrixVo matrixVo);

    @Override
    public JSONObject searchTableData(MatrixDataVo dataVo) {
        return myTableDataSearch(dataVo);
    }

    protected abstract JSONObject myTableDataSearch(MatrixDataVo dataVo);

    @Override
    public List<Map<String, JSONObject>> searchTableDataNew(MatrixDataVo dataVo) {
        /** 属性集合去重 **/
        List<String> distinctColumList = new ArrayList<>();
        for (String column : dataVo.getColumnList()) {
            if (!distinctColumList.contains(column)) {
                distinctColumList.add(column);
            }
        }
        dataVo.setColumnList(distinctColumList);
        List<Map<String, JSONObject>> resultList = mySearchTableDataNew(dataVo);
        return resultList;
    }

    protected List<Map<String, JSONObject>> mySearchTableDataNew(MatrixDataVo dataVo) {
        return null;
    }

    @Override
    public void saveTableRowData(String matrixUuid, JSONObject rowData) {
        mySaveTableRowData(matrixUuid, rowData);
    }

    protected abstract JSONObject mySaveTableRowData(String matrixUuid, JSONObject rowData);

    @Override
    public Map<String, String> getTableRowData(MatrixDataVo matrixDataVo) {
        return myGetTableRowData(matrixDataVo);
    }

    protected abstract Map<String, String> myGetTableRowData(MatrixDataVo matrixDataVo);

    @Override
    public void deleteTableRowData(String matrixUuid, List<String> uuidList) {
        myDeleteTableRowData(matrixUuid, uuidList);
    }

    protected abstract void myDeleteTableRowData(String matrixUuid, List<String> uuidList);

    @Override
    public void moveTableRowDataSort(String matrixUuid, String uuid, String toUuid) {
        myMoveTableRowDataSort(matrixUuid, uuid, toUuid);
    }

    protected void myMoveTableRowDataSort(String matrixUuid, String uuid, String toUuid) {

    }

    protected JSONArray getTheadList(List<MatrixAttributeVo> attributeList) {
        JSONArray theadList = new JSONArray();
        JSONObject selectionObj = new JSONObject();
        selectionObj.put("key", "selection");
        selectionObj.put("width", 60);
        theadList.add(selectionObj);
        for (MatrixAttributeVo attributeVo : attributeList) {
            JSONObject columnObj = new JSONObject();
            columnObj.put("title", attributeVo.getName());
            columnObj.put("key", attributeVo.getUuid());
            theadList.add(columnObj);
        }
        JSONObject actionObj = new JSONObject();
        actionObj.put("title", "");
        actionObj.put("key", "action");
        actionObj.put("align", "right");
        actionObj.put("width", 10);
        theadList.add(actionObj);
        return theadList;
    }

    protected JSONArray getTheadList(String matrixUuid, List<MatrixAttributeVo> attributeList, List<String> columnList) {
        Map<String, MatrixAttributeVo> attributeMap = new HashMap<>();
        for (MatrixAttributeVo attribute : attributeList) {
            attributeMap.put(attribute.getUuid(), attribute);
        }
        JSONArray theadList = new JSONArray();
        for (String column : columnList) {
            MatrixAttributeVo attribute = attributeMap.get(column);
            if (attribute == null) {
                throw new MatrixAttributeNotFoundException(matrixUuid, column);
            }
            JSONObject theadObj = new JSONObject();
            theadObj.put("key", attribute.getUuid());
            theadObj.put("title", attribute.getName());
            theadList.add(theadObj);
        }
        return theadList;
    }

    /**
     * 合并filterList和sourceColumnList
     * @param dataVo
     */
    protected boolean mergeFilterListAndSourceColumnList(MatrixDataVo dataVo) {
        List<MatrixFilterVo> filterList = dataVo.getFilterList();
        if (CollectionUtils.isEmpty(filterList)) {
            return true;
        }
        List<MatrixColumnVo> sourceColumnList = dataVo.getSourceColumnList();
        Map<String, MatrixColumnVo> sourceColumnMap = sourceColumnList.stream().collect(Collectors.toMap(e -> e.getColumn(), e -> e));
        for (MatrixFilterVo matrixFilterVo : filterList) {
            if (matrixFilterVo == null) {
                continue;
            }
            String uuid = matrixFilterVo.getUuid();
            if (StringUtils.isBlank(uuid)) {
                continue;
            }
            List<String> filterValueList = matrixFilterVo.getValueList();
            if (CollectionUtils.isEmpty(filterValueList)) {
                continue;
            }
            MatrixColumnVo sourceColumnVo = sourceColumnMap.get(uuid);
            if (sourceColumnVo != null) {
                List<String> valueList = sourceColumnVo.getValueList();
                String expression = sourceColumnVo.getExpression();
                if (Objects.equals(expression, Expression.EQUAL.getExpression()) || Objects.equals(expression, Expression.INCLUDE.getExpression())) {
                    valueList.retainAll(filterValueList);
                    if (CollectionUtils.isEmpty(valueList)) {
                        return false;
                    }
                    continue;
                } else if (Objects.equals(expression, Expression.UNEQUAL.getExpression()) || Objects.equals(expression, Expression.EXCLUDE.getExpression())) {
                    filterValueList.retainAll(valueList);
                    if (CollectionUtils.isEmpty(filterValueList)) {
                        return false;
                    }
                }
            }
            MatrixColumnVo sourceColumn = new MatrixColumnVo();
            sourceColumn.setColumn(uuid);
            sourceColumn.setExpression(Expression.INCLUDE.getExpression());
            sourceColumn.setValueList(filterValueList);
            sourceColumnList.add(sourceColumn);
        }
        return true;
    }
}
