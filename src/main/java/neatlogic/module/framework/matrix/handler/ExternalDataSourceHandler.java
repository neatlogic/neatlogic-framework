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

package neatlogic.module.framework.matrix.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.integration.IntegrationHandlerNotFoundException;
import neatlogic.framework.exception.integration.IntegrationNotFoundException;
import neatlogic.framework.exception.type.ParamNotExistsException;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.integration.core.RequestFrom;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.constvalue.MatrixType;
import neatlogic.framework.matrix.constvalue.SearchExpression;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerBase;
import neatlogic.framework.matrix.dto.*;
import neatlogic.framework.matrix.exception.MatrixExternalAccessException;
import neatlogic.framework.matrix.exception.MatrixExternalNotFoundException;
import neatlogic.framework.util.ExcelUtil;
import neatlogic.framework.util.javascript.JavascriptUtil;
import neatlogic.module.framework.integration.handler.FrameworkRequestFrom;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author linbq
 * @since 2021/11/4 19:34
 **/
@Component
public class ExternalDataSourceHandler extends MatrixDataSourceHandlerBase {

    private final static Logger logger = LoggerFactory.getLogger(ExternalDataSourceHandler.class);

    @Resource
    private IntegrationMapper integrationMapper;

    @Override
    public String getHandler() {
        return MatrixType.EXTERNAL.getValue();
    }

    @Override
    protected boolean mySaveMatrix(MatrixVo matrixVo) throws Exception {
        String integrationUuid = matrixVo.getIntegrationUuid();
        if (StringUtils.isBlank(integrationUuid)) {
            throw new ParamNotExistsException("integrationUuid");
        }
        MatrixExternalVo oldExternalVo = matrixMapper.getMatrixExternalByMatrixUuid(matrixVo.getUuid());
        if (oldExternalVo != null) {
            if (integrationUuid.equals(oldExternalVo.getIntegrationUuid())) {
                return false;
            }
        }
//        validateMatrixExternalData(integrationUuid);
        MatrixExternalVo externalVo = new MatrixExternalVo(matrixVo.getUuid(), integrationUuid);
        matrixMapper.replaceMatrixExternal(externalVo);
        return true;
    }

    @Override
    protected void myGetMatrix(MatrixVo matrixVo) {
        MatrixExternalVo matrixExternalVo = matrixMapper.getMatrixExternalByMatrixUuid(matrixVo.getUuid());
        if (matrixExternalVo == null) {
            throw new MatrixExternalNotFoundException(matrixVo.getName());
        }
        matrixVo.setIntegrationUuid(matrixExternalVo.getIntegrationUuid());
    }

    @Override
    protected void myDeleteMatrix(String uuid) {
        matrixMapper.deleteMatrixExternalByMatrixUuid(uuid);
    }

    @Override
    protected void myCopyMatrix(String sourceUuid, MatrixVo matrixVo) {

    }

    @Override
    protected JSONObject myImportMatrix(MatrixVo matrixVo, MultipartFile multipartFile) throws IOException {
        return null;
    }

    @Override
    protected Workbook myExportMatrix2Excel(MatrixVo matrixVo) {
        HSSFWorkbook workbook = null;
        MatrixExternalVo externalVo = matrixMapper.getMatrixExternalByMatrixUuid(matrixVo.getUuid());
        if (externalVo == null) {
            throw new MatrixExternalNotFoundException(matrixVo.getName());
        }
        IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(externalVo.getIntegrationUuid());
        IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(integrationVo.getHandler());
        if (handler == null) {
            throw new IntegrationHandlerNotFoundException(integrationVo.getHandler());
        }

        IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
        if (StringUtils.isNotBlank(resultVo.getError())) {
            logger.error(resultVo.getError());
            throw new MatrixExternalAccessException(integrationVo.getName());
        } else if (StringUtils.isNotBlank(resultVo.getTransformedResult())) {
            JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
            if (MapUtils.isNotEmpty(transformedResult)) {
                List<String> headerList = new ArrayList<>();
                List<String> columnList = new ArrayList<>();
                JSONArray theadList = transformedResult.getJSONArray("theadList");
                if (CollectionUtils.isNotEmpty(theadList)) {
                    for (int i = 0; i < theadList.size(); i++) {
                        JSONObject obj = theadList.getJSONObject(i);
                        headerList.add(obj.getString("title"));
                        columnList.add(obj.getString("key"));
                    }
                }
                List<Map<String, String>> dataMapList = (List<Map<String, String>>) transformedResult.get("tbodyList");
                workbook = ExcelUtil.createExcel(workbook, headerList, columnList, null, dataMapList);
            }
        }
        return workbook;
    }

    @Override
    protected MatrixVo myExportMatrix(MatrixVo matrixVo) {
        myGetMatrix(matrixVo);
        return matrixVo;
    }

    @Override
    protected void myImportMatrix(MatrixVo matrixVo) {
        MatrixExternalVo externalVo = new MatrixExternalVo(matrixVo.getUuid(), matrixVo.getIntegrationUuid());
        matrixMapper.replaceMatrixExternal(externalVo);
    }

    @Override
    protected void mySaveAttributeList(String matrixUuid, List<MatrixAttributeVo> matrixAttributeList) {

    }

    @Override
    protected List<MatrixAttributeVo> myGetAttributeList(MatrixVo matrixVo) {
        MatrixExternalVo externalVo = matrixMapper.getMatrixExternalByMatrixUuid(matrixVo.getUuid());
        if (externalVo == null) {
            throw new MatrixExternalNotFoundException(matrixVo.getName());
        }
        IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(externalVo.getIntegrationUuid());
        if (integrationVo == null) {
            throw new IntegrationNotFoundException(externalVo.getIntegrationUuid());
        }
        IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(integrationVo.getHandler());
        if (handler == null) {
            throw new IntegrationHandlerNotFoundException(integrationVo.getHandler());
        }
        return getExternalMatrixAttributeList(matrixVo.getUuid(), integrationVo);
    }

    @Override
    protected JSONObject myExportAttribute(MatrixVo matrixVo) {
        return null;
    }

    @Override
    protected JSONObject myTableDataSearch(MatrixDataVo dataVo) {
        JSONObject returnObj = new JSONObject();
        MatrixExternalVo externalVo = matrixMapper.getMatrixExternalByMatrixUuid(dataVo.getMatrixUuid());
        if (externalVo == null) {
            throw new MatrixExternalNotFoundException(dataVo.getMatrixUuid());
        }
        IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(externalVo.getIntegrationUuid());
        if (integrationVo == null) {
            throw new IntegrationNotFoundException(externalVo.getIntegrationUuid());
        }
        IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(integrationVo.getHandler());
        if (handler == null) {
            throw new IntegrationHandlerNotFoundException(integrationVo.getHandler());
        }
        List<MatrixAttributeVo> matrixAttributeList = getExternalMatrixAttributeList(dataVo.getMatrixUuid(), integrationVo);
        if (CollectionUtils.isNotEmpty(matrixAttributeList)) {
            List<Map<String, JSONObject>> tbodyList = new ArrayList<>();
            JSONArray defaultValue = dataVo.getDefaultValue();
            if (CollectionUtils.isNotEmpty(defaultValue)) {
//                String uuidColumn = dataVo.getUuidColumn();
//                boolean uuidColumnExist = false;
//                for (MatrixAttributeVo matrixAttributeVo : matrixAttributeList) {
//                    if (Objects.equals(matrixAttributeVo.getUuid(), uuidColumn)) {
//                        uuidColumnExist = true;
//                    }
//                }
//                if (!uuidColumnExist) {
//                    throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), uuidColumn);
//                }
//                List<MatrixColumnVo> sourceColumnList = new ArrayList<>();
//                MatrixColumnVo sourceColumnVo = new MatrixColumnVo();
//                sourceColumnVo.setColumn(uuidColumn);
//                List<String> uuidList = defaultValue.toJavaList(String.class);
//                for (String uuidValue : uuidList) {
//                    List<String> valueList = new ArrayList<>();
//                    valueList.add(uuidValue);
//                    sourceColumnVo.setValueList(valueList);
//                    sourceColumnVo.setExpression(Expression.EQUAL.getExpression());
//                    sourceColumnList.clear();
//                    sourceColumnList.add(sourceColumnVo);
//                    integrationVo.getParamObj().put("sourceColumnList", sourceColumnList);
//                    IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
//                    if (StringUtils.isNotBlank(resultVo.getError())) {
//                        logger.error(resultVo.getError());
//                        throw new MatrixExternalAccessException();
//                    }
//                    handler.validate(resultVo);
//                    List<Map<String, JSONObject>> externalDataTbodyList = getExternalDataTbodyList(resultVo, dataVo.getColumnList(), dataVo);
//                    for (Map<String, JSONObject> tbody : externalDataTbodyList) {
//                        JSONObject valueObj = tbody.get(uuidColumn);
//                        if (MapUtils.isNotEmpty(valueObj)) {
//                            String value = valueObj.getString("value");
//                            if (Objects.equals(value, uuidValue)) {
//                                tbodyList.add(tbody);
//                                break;
//                            }
//                        }
//                    }
//                }
                integrationVo.getParamObj().put("defaultValue", defaultValue);
                IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
                if (StringUtils.isNotBlank(resultVo.getError())) {
                    logger.error(resultVo.getError());
                    throw new MatrixExternalAccessException(integrationVo.getName());
                }
                handler.validate(resultVo);
                JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
                Integer rowNum = transformedResult.getInteger("rowNum");
                dataVo.setRowNum(rowNum);
                JSONArray tbodyArray = transformedResult.getJSONArray("tbodyList");
                List<Map<String, JSONObject>> externalDataTbodyList = getExternalDataTbodyList(matrixAttributeList, tbodyArray, dataVo.getColumnList());
                tbodyList.addAll(externalDataTbodyList);
                returnObj.put("tbodyList", tbodyList);
            } else {
                if (!mergeFilterListAndSourceColumnList(dataVo)) {
                    return returnObj;
                }
                JSONObject paramObj = integrationVo.getParamObj();
                paramObj.put("currentPage", dataVo.getCurrentPage());
                int pageSize = dataVo.getPageSize();
                paramObj.put("pageSize", pageSize);
                paramObj.put("needPage", pageSize < 100);
                paramObj.put("sourceColumnList", dataVo.getSourceColumnList());
                IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
                if (StringUtils.isNotBlank(resultVo.getError())) {
                    logger.error(resultVo.getError());
                    throw new MatrixExternalAccessException(integrationVo.getName());
                }
                handler.validate(resultVo);
                JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
                Integer rowNum = transformedResult.getInteger("rowNum");
                dataVo.setRowNum(rowNum);
                returnObj.put("currentPage", transformedResult.get("currentPage"));
                returnObj.put("pageSize", transformedResult.get("pageSize"));
                returnObj.put("pageCount", transformedResult.get("pageCount"));
                returnObj.put("rowNum", rowNum);
                JSONArray tbodyArray = transformedResult.getJSONArray("tbodyList");
                tbodyList = getExternalDataTbodyList(matrixAttributeList, tbodyArray, dataVo.getColumnList());
                returnObj.put("tbodyList", tbodyList);
            }
            JSONArray theadList = getTheadList(dataVo.getMatrixUuid(), matrixAttributeList, dataVo.getColumnList());
            returnObj.put("theadList", theadList);
            /** 将arrayColumnList包含的属性值转成数组 **/
            List<String> arrayColumnList = dataVo.getArrayColumnList();
            if (CollectionUtils.isNotEmpty(arrayColumnList) && CollectionUtils.isNotEmpty(tbodyList)) {
                arrayColumnDataConversion(arrayColumnList, tbodyList);
            }
        }
        return returnObj;
    }

    @Override
    protected List<Map<String, JSONObject>> mySearchTableDataNew(MatrixDataVo dataVo) {
        MatrixExternalVo externalVo = matrixMapper.getMatrixExternalByMatrixUuid(dataVo.getMatrixUuid());
        if (externalVo == null) {
            throw new MatrixExternalNotFoundException(dataVo.getMatrixUuid());
        }
        IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(externalVo.getIntegrationUuid());
        IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(integrationVo.getHandler());
        if (handler == null) {
            throw new IntegrationHandlerNotFoundException(integrationVo.getHandler());
        }
        JSONArray tbodyArray = new JSONArray();
        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        List<MatrixAttributeVo> matrixAttributeList = getExternalMatrixAttributeList(dataVo.getMatrixUuid(), integrationVo);
        if (CollectionUtils.isEmpty(matrixAttributeList)) {
            return resultList;
        }
        JSONObject paramObj = integrationVo.getParamObj();

        JSONArray defaultValue = dataVo.getDefaultValue();
        if (CollectionUtils.isNotEmpty(defaultValue)) {
            paramObj.put("defaultValue", defaultValue);
            IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
            if (StringUtils.isNotBlank(resultVo.getError())) {
                logger.error(resultVo.getError());
                throw new MatrixExternalAccessException(integrationVo.getName());
            }
            handler.validate(resultVo);
            JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
            Integer rowNum = transformedResult.getInteger("rowNum");
            dataVo.setRowNum(rowNum);
            tbodyArray = transformedResult.getJSONArray("tbodyList");
        } else if (CollectionUtils.isNotEmpty(dataVo.getDefaultValueFilterList())) {
            for (MatrixDefaultValueFilterVo defaultValueFilterVo : dataVo.getDefaultValueFilterList()) {
                List<MatrixFilterVo> filterList = new ArrayList<>();
                MatrixKeywordFilterVo valueFieldFilter = defaultValueFilterVo.getValueFieldFilter();
                if (valueFieldFilter != null) {
                    filterList.add(new MatrixFilterVo(valueFieldFilter.getUuid(), valueFieldFilter.getExpression(), Arrays.asList(valueFieldFilter.getValue())));
                }
                MatrixKeywordFilterVo textFieldFilter = defaultValueFilterVo.getTextFieldFilter();
                if (textFieldFilter != null && (valueFieldFilter == null || !Objects.equals(valueFieldFilter.getUuid(), textFieldFilter.getUuid()))) {
                    filterList.add(new MatrixFilterVo(textFieldFilter.getUuid(), textFieldFilter.getExpression(), Arrays.asList(textFieldFilter.getValue())));
                }
//                dataVo.setFilterList(filterList);
                paramObj.put("filterList", filterList);
                IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
                if (StringUtils.isNotBlank(resultVo.getError())) {
                    logger.error(resultVo.getError());
                    throw new MatrixExternalAccessException(integrationVo.getName());
                }
                handler.validate(resultVo);
                JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
                tbodyArray.addAll(transformedResult.getJSONArray("tbodyList"));
            }
        } else {
            List<MatrixFilterVo> filterList = dataVo.getFilterList();
            String keywordColumn = dataVo.getKeywordColumn();
            if (StringUtils.isNotBlank(keywordColumn) && StringUtils.isNotBlank(dataVo.getKeyword())) {
//                paramObj.put("keyword", dataVo.getKeyword());
                filterList.add(new MatrixFilterVo(keywordColumn, SearchExpression.LI.getExpression(), Arrays.asList(dataVo.getKeyword())));
            }
            //下面逻辑适用于下拉框滚动加载，也可以搜索，但是一页返回的数据量可能会小于pageSize，因为做了去重处理
            paramObj.put("currentPage", dataVo.getCurrentPage());
            paramObj.put("pageSize", dataVo.getPageSize());
            paramObj.put("filterList", filterList);
            IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
            if (StringUtils.isNotBlank(resultVo.getError())) {
                logger.error(resultVo.getError());
                throw new MatrixExternalAccessException(integrationVo.getName());
            }
            handler.validate(resultVo);
            JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
            Integer rowNum = transformedResult.getInteger("rowNum");
            dataVo.setRowNum(rowNum);
            tbodyArray = transformedResult.getJSONArray("tbodyList");
        }
        if (CollectionUtils.isEmpty(tbodyArray)) {
            return resultList;
        }
        JSONArray distinctList = new JSONArray();
        for (Object dataMap : tbodyArray) {
            if(distinctList.contains(dataMap)){
                continue;
            }
            distinctList.add(dataMap);
        }
        return getExternalDataTbodyList(matrixAttributeList, distinctList, dataVo.getColumnList());
    }

    @Override
    protected JSONObject mySaveTableRowData(String matrixUuid, JSONObject rowData) {
        return null;
    }

    @Override
    protected Map<String, String> myGetTableRowData(MatrixDataVo matrixDataVo) {
        return null;
    }

    @Override
    protected void myDeleteTableRowData(String matrixUuid, List<String> uuidList) {

    }

    private List<MatrixAttributeVo> getExternalMatrixAttributeList(String matrixUuid, IntegrationVo integrationVo) {
        List<MatrixAttributeVo> matrixAttributeList = new ArrayList<>();
        JSONObject config = integrationVo.getConfig();
        if (MapUtils.isNotEmpty(config)) {
            JSONObject output = config.getJSONObject("output");
            if (MapUtils.isNotEmpty(output)) {
                String content = output.getString("content");
                if (StringUtils.isNotBlank(content)) {
                    try {
                        content = JavascriptUtil.transform(new JSONObject(), content);
                        JSONObject contentObj = JSON.parseObject(content);
                        if (MapUtils.isNotEmpty(contentObj)) {
                            JSONArray theadList = contentObj.getJSONArray("theadList");
                            if (CollectionUtils.isNotEmpty(theadList)) {
                                for (int i = 0; i < theadList.size(); i++) {
                                    JSONObject theadObj = theadList.getJSONObject(i);
                                    MatrixAttributeVo matrixAttributeVo = new MatrixAttributeVo();
                                    matrixAttributeVo.setMatrixUuid(matrixUuid);
                                    matrixAttributeVo.setUuid(theadObj.getString("key"));
                                    matrixAttributeVo.setName(theadObj.getString("title"));
                                    matrixAttributeVo.setType(MatrixAttributeType.INPUT.getValue());
                                    matrixAttributeVo.setIsDeletable(0);
                                    matrixAttributeVo.setSort(i);
                                    matrixAttributeVo.setIsRequired(0);
                                    Integer primaryKey = theadObj.getInteger("primaryKey");
                                    if (Objects.equals(primaryKey, 1)) {
                                        matrixAttributeVo.setPrimaryKey(1);
                                    }
                                    Integer isSearchable = theadObj.getInteger("isSearchable");
                                    matrixAttributeVo.setIsSearchable((isSearchable == null || isSearchable.intValue() != 1) ? 0 : 1);
                                    matrixAttributeList.add(matrixAttributeVo);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        return matrixAttributeList;
    }

    private List<Map<String, JSONObject>> getExternalDataTbodyList(List<MatrixAttributeVo> attributeList, JSONArray tbodyList, List<String> columnList) {
        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tbodyList)) {
            String uuidColumn = null;
            for (MatrixAttributeVo matrixAttributeVo : attributeList) {
                if (Objects.equals(matrixAttributeVo.getPrimaryKey(), 1)) {
                    uuidColumn = matrixAttributeVo.getUuid();
                }
            }
            for (int i = 0; i < tbodyList.size(); i++) {
                JSONObject rowData = tbodyList.getJSONObject(i);
                Map<String, JSONObject> resultMap = new HashMap<>();
                for (String column : columnList) {
                    String columnValue = rowData.getString(column);
                    if (columnValue == null) {
                        columnValue = "";
                    }
                    JSONObject resultObj = new JSONObject();
                    resultObj.put("type", MatrixAttributeType.INPUT.getValue());
                    resultObj.put("value", columnValue);
                    resultObj.put("text", columnValue);
                    resultMap.put(column, resultObj);
                }
                if (StringUtils.isNotBlank(uuidColumn)) {
                    if (!"uuid".equals(uuidColumn) || !columnList.contains(uuidColumn)) {
                        String columnValue = rowData.getString(uuidColumn);
                        if (columnValue == null) {
                            columnValue = "";
                        }
                        JSONObject resultObj = new JSONObject();
                        resultObj.put("type", MatrixAttributeType.INPUT.getValue());
                        resultObj.put("value", columnValue);
                        resultObj.put("text", columnValue);
                        resultMap.put("uuid", resultObj);
                    }
                }
                resultList.add(resultMap);
            }
        }
        return resultList;
    }

    /**
     * @param arrayColumnList 需要将值转化成数组的属性集合
     * @param tbodyList       表格数据
     * @return void
     * @Time:2020年7月8日
     * @Description: 将arrayColumnList包含的属性值转成数组
     */
    private void arrayColumnDataConversion(List<String> arrayColumnList, List<Map<String, JSONObject>> tbodyList) {
        for (Map<String, JSONObject> rowData : tbodyList) {
            for (Map.Entry<String, JSONObject> entry : rowData.entrySet()) {
                if (arrayColumnList.contains(entry.getKey())) {
                    List<ValueTextVo> valueObjList = new ArrayList<>();
                    JSONObject valueObj = entry.getValue();
                    String value = valueObj.getString("value");
                    if (StringUtils.isNotBlank(value)) {
                        if (value.startsWith("[") && value.endsWith("]")) {
                            JSONArray valueArray = valueObj.getJSONArray("value");
                            if (CollectionUtils.isNotEmpty(valueArray)) {
                                for (int i = 0; i < valueArray.size(); i++) {
                                    Object valueObject = valueArray.get(i);
                                    if (valueObject instanceof JSONObject) {
                                        JSONObject valueJsonObject = (JSONObject) valueObject;
                                        Object valueStr = valueJsonObject.get("value");
                                        String textStr = valueJsonObject.getString("text");
                                        if (valueStr != null && StringUtils.isNotBlank(textStr)) {
                                            valueObjList.add(new ValueTextVo(valueStr, textStr));
                                        }
                                    } else if (valueObject instanceof String) {
                                        String valueStr = (String) valueObject;
                                        valueObjList.add(new ValueTextVo(valueStr, valueStr));
                                    }
                                }
                            }
                        } else {
                            valueObjList.add(new ValueTextVo(value, value));
                        }
                    }
                    valueObj.put("dataList", valueObjList);
                    valueObj.put("type", "selects");
                }
            }
        }
    }

    /**
     * 校验集成接口数据是否符合矩阵格式
     *
     * @param integrationUuid 集成配置uuid
     * @throws ApiRuntimeException
     */
    private void validateMatrixExternalData(String integrationUuid) throws ApiRuntimeException {
        IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(integrationUuid);
        if (integrationVo == null) {
            throw new IntegrationNotFoundException(integrationUuid);
        }
        IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(integrationVo.getHandler());
        if (handler == null) {
            throw new IntegrationHandlerNotFoundException(integrationVo.getHandler());
        }
        IntegrationResultVo resultVo = handler.sendRequest(integrationVo, FrameworkRequestFrom.TEST);
        if (StringUtils.isNotBlank(resultVo.getError())) {
            throw new MatrixExternalAccessException(integrationVo.getName());
        }
        handler.validate(resultVo);
    }
}
