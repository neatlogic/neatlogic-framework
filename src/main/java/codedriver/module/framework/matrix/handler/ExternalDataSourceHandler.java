/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.matrix.handler;

import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.exception.integration.IntegrationHandlerNotFoundException;
import codedriver.framework.exception.integration.IntegrationNotFoundException;
import codedriver.framework.exception.type.ParamNotExistsException;
import codedriver.framework.integration.core.IIntegrationHandler;
import codedriver.framework.integration.core.IntegrationHandlerFactory;
import codedriver.framework.integration.core.RequestFrom;
import codedriver.framework.integration.dao.mapper.IntegrationMapper;
import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.matrix.constvalue.MatrixType;
import codedriver.framework.matrix.core.MatrixDataSourceHandlerBase;
import codedriver.framework.matrix.dto.*;
import codedriver.framework.matrix.exception.MatrixAttributeNotFoundException;
import codedriver.framework.matrix.exception.MatrixExternalAccessException;
import codedriver.framework.matrix.exception.MatrixExternalNotFoundException;
import codedriver.framework.util.ExcelUtil;
import codedriver.module.framework.matrix.service.MatrixService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linbq
 * @since 2021/11/4 19:34
 **/
@Component
public class ExternalDataSourceHandler extends MatrixDataSourceHandlerBase {

    private final static Logger logger = LoggerFactory.getLogger(ExternalDataSourceHandler.class);

    @Resource
    private IntegrationMapper integrationMapper;

    @Resource
    private MatrixService matrixService;

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
        matrixService.validateMatrixExternalData(integrationUuid);
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
    protected HSSFWorkbook myExportMatrix(MatrixVo matrixVo) {
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
            throw new MatrixExternalAccessException();
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
        return matrixService.getExternalMatrixAttributeList(matrixVo.getUuid(), integrationVo);
    }

    @Override
    protected JSONObject myExportAttribute(MatrixVo matrixVo) {
        return null;
    }

    @Override
    protected JSONObject myGetTableData(MatrixDataVo dataVo) {

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

        JSONObject paramObj = integrationVo.getParamObj();
        paramObj.put("keyword", dataVo.getKeyword());
        paramObj.put("currentPage", dataVo.getCurrentPage());
        paramObj.put("pageSize", dataVo.getPageSize());
        IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
        if (StringUtils.isNotBlank(resultVo.getError())) {
            logger.error(resultVo.getError());
            throw new MatrixExternalAccessException();
        }
        handler.validate(resultVo);
        JSONObject returnObj = new JSONObject();
        JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
        returnObj.put("currentPage", transformedResult.get("currentPage"));
        returnObj.put("pageSize", transformedResult.get("pageSize"));
        returnObj.put("pageCount", transformedResult.get("pageCount"));
        returnObj.put("rowNum", transformedResult.get("rowNum"));
        returnObj.put("theadList", transformedResult.get("theadList"));
//            returnObj.putAll(transformedResult);
        List<Map<String, Object>> tbodyList = new ArrayList<>();
        JSONArray tbodyArray = transformedResult.getJSONArray("tbodyList");
        if (CollectionUtils.isNotEmpty(tbodyArray)) {
            for (int i = 0; i < tbodyArray.size(); i++) {
                JSONObject rowData = tbodyArray.getJSONObject(i);
                if (MapUtils.isNotEmpty(rowData)) {
                    Map<String, Object> rowDataMap = new HashMap<>();
                    for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                        rowDataMap.put(entry.getKey(), matrixService.matrixAttributeValueHandle(entry.getValue()));
                    }
                    tbodyList.add(rowDataMap);
                }
            }
        }
        returnObj.put("tbodyList", tbodyList);
        return returnObj;
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
        List<MatrixAttributeVo> matrixAttributeList = matrixService.getExternalMatrixAttributeList(dataVo.getMatrixUuid(), integrationVo);
        if (CollectionUtils.isNotEmpty(matrixAttributeList)) {
            List<Map<String, JSONObject>> tbodyList = new ArrayList<>();
            JSONArray dafaultValue = dataVo.getDefaultValue();
            if (CollectionUtils.isNotEmpty(dafaultValue)) {
//                String uuidColumn = jsonObj.getString("uuidColumn");
                String uuidColumn = dataVo.getUuidColumn();
                boolean uuidColumnExist = false;
                for (MatrixAttributeVo matrixAttributeVo : matrixAttributeList) {
                    if (Objects.equals(matrixAttributeVo.getUuid(), uuidColumn)) {
                        uuidColumnExist = true;
                    }
                }
                if (!uuidColumnExist) {
                    throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), uuidColumn);
                }
                List<MatrixColumnVo> sourceColumnList = new ArrayList<>();
                MatrixColumnVo sourceColumnVo = new MatrixColumnVo();
                sourceColumnVo.setColumn(uuidColumn);
                List<String> uuidList = dafaultValue.toJavaList(String.class);
                for (String uuidValue : uuidList) {
                    sourceColumnVo.setValue(uuidValue);
                    sourceColumnVo.setExpression(Expression.EQUAL.getExpression());
                    sourceColumnList.clear();
                    sourceColumnList.add(sourceColumnVo);
                    integrationVo.getParamObj().put("sourceColumnList", sourceColumnList);
                    IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
                    if (StringUtils.isNotBlank(resultVo.getError())) {
                        logger.error(resultVo.getError());
                        throw new MatrixExternalAccessException();
                    }
                    handler.validate(resultVo);
                    tbodyList.addAll(matrixService.getExternalDataTbodyList(resultVo, dataVo.getColumnList()));
                }
                returnObj.put("tbodyList", tbodyList);
            } else {
//                jsonObj.put("sourceColumnList", dataVo.getSourceColumnList()); //防止集成管理 js length 异常
//            integrationVo.getParamObj().putAll(jsonObj);
                JSONObject paramObj = integrationVo.getParamObj();
                paramObj.put("currentPage", dataVo.getCurrentPage());
                int pageSize = dataVo.getPageSize();
                paramObj.put("pageSize", pageSize);
                paramObj.put("needPage", pageSize < 100);
                paramObj.put("filterList", dataVo.getFilterList());
                paramObj.put("sourceColumnList", dataVo.getSourceColumnList());
                IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
                if (StringUtils.isNotBlank(resultVo.getError())) {
                    logger.error(resultVo.getError());
                    throw new MatrixExternalAccessException();
                }
                handler.validate(resultVo);
                JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
                returnObj.put("currentPage", transformedResult.get("currentPage"));
                returnObj.put("pageSize", transformedResult.get("pageSize"));
                returnObj.put("pageCount", transformedResult.get("pageCount"));
                returnObj.put("rowNum", transformedResult.get("rowNum"));
                tbodyList = matrixService.getExternalDataTbodyList(resultVo, dataVo.getColumnList());
                /** 将arrayColumnList包含的属性值转成数组 **/
//                JSONArray arrayColumnArray = jsonObj.getJSONArray("arrayColumnList");
//                if (CollectionUtils.isNotEmpty(arrayColumnArray)) {
//                    List<String> arrayColumnList = arrayColumnArray.toJavaList(String.class);
//                    if (CollectionUtils.isNotEmpty(tbodyList)) {
//                        matrixService.arrayColumnDataConversion(arrayColumnList, tbodyList);
//                    }
//                }
                returnObj.put("tbodyList", tbodyList);
//            returnObj.put("searchColumnDetailList", getSearchColumnDetailList(dataVo.getMatrixUuid(), matrixAttributeList, searchColumnArray));
            }
            JSONArray theadList = getTheadList(dataVo.getMatrixUuid(), matrixAttributeList, dataVo.getColumnList());
            returnObj.put("theadList", theadList);
            List<String> arrayColumnList = dataVo.getArrayColumnList();
            if (CollectionUtils.isNotEmpty(arrayColumnList) && CollectionUtils.isNotEmpty(tbodyList)) {
                matrixService.arrayColumnDataConversion(arrayColumnList, tbodyList);
            }
        }
        return returnObj;
    }

    @Override
    protected List<Map<String, JSONObject>> myTableColumnDataSearch(MatrixDataVo dataVo) {
        MatrixExternalVo externalVo = matrixMapper.getMatrixExternalByMatrixUuid(dataVo.getMatrixUuid());
        if (externalVo == null) {
            throw new MatrixExternalNotFoundException(dataVo.getMatrixUuid());
        }
        IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(externalVo.getIntegrationUuid());
        IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(integrationVo.getHandler());
        if (handler == null) {
            throw new IntegrationHandlerNotFoundException(integrationVo.getHandler());
        }

        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        List<MatrixAttributeVo> matrixAttributeList = matrixService.getExternalMatrixAttributeList(dataVo.getMatrixUuid(), integrationVo);
        if (CollectionUtils.isNotEmpty(matrixAttributeList)) {
            List<String> attributeList = matrixAttributeList.stream().map(MatrixAttributeVo::getUuid).collect(Collectors.toList());
            List<String> columnList = dataVo.getColumnList();
            for (String column : columnList) {
                if (!attributeList.contains(column)) {
                    throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), column);
                }
            }
            JSONObject paramObj = integrationVo.getParamObj();
            List<MatrixColumnVo> sourceColumnList = new ArrayList<>();
//            jsonObj.put("sourceColumnList", sourceColumnList); //防止集成管理 js length 异常

            JSONArray defaultValue = dataVo.getDefaultValue();
            if (CollectionUtils.isNotEmpty(defaultValue)) {
                for (String value : defaultValue.toJavaList(String.class)) {
                    if (value.contains(SELECT_COMPOSE_JOINER)) {
                        String[] split = value.split(SELECT_COMPOSE_JOINER);
                        //当下拉框配置的值和显示文字列为同一列时，value值是这样的20210101&=&20210101，split数组第一和第二个元素相同，这时需要去重
                        List<String> splitList = new ArrayList<>();
                        for (String str : split) {
                            if (!splitList.contains(str)) {
                                splitList.add(str);
                            }
                        }
                        int min = Math.min(splitList.size(), columnList.size());
                        for (int i = 0; i < min; i++) {
                            String column = columnList.get(i);
                            if (StringUtils.isNotBlank(column)) {
                                MatrixColumnVo matrixColumnVo = new MatrixColumnVo(column, splitList.get(i));
                                matrixColumnVo.setExpression(Expression.EQUAL.getExpression());
                                sourceColumnList.add(matrixColumnVo);
                            }
                        }
//                        integrationVo.getParamObj().putAll(jsonObj);
                        paramObj.put("sourceColumnList", sourceColumnList);
                        IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
                        if (StringUtils.isNotBlank(resultVo.getError())) {
                            logger.error(resultVo.getError());
                            throw new MatrixExternalAccessException();
                        }
                        resultList.addAll(matrixService.getExternalDataTbodyList(resultVo, columnList));
                    }
                }
            } else {
                String keywordColumn = dataVo.getKeywordColumn();
                if (StringUtils.isNotBlank(keywordColumn) && StringUtils.isNotBlank(dataVo.getKeyword())) {
                    if (!attributeList.contains(keywordColumn)) {
                        throw new MatrixAttributeNotFoundException(dataVo.getMatrixUuid(), keywordColumn);
                    }
                    MatrixColumnVo matrixColumnVo = new MatrixColumnVo();
                    matrixColumnVo.setColumn(keywordColumn);
                    matrixColumnVo.setExpression(Expression.LIKE.getExpression());
                    matrixColumnVo.setValue(dataVo.getKeyword());
                    sourceColumnList.add(matrixColumnVo);
                }
//                integrationVo.getParamObj().putAll(jsonObj);
                paramObj.put("currentPage", dataVo.getCurrentPage());
                int pageSize = dataVo.getPageSize();
                paramObj.put("pageSize", pageSize);
                paramObj.put("needPage", pageSize < 100);
                paramObj.put("filterList", dataVo.getFilterList());
                paramObj.put("sourceColumnList", sourceColumnList);
                IntegrationResultVo resultVo = handler.sendRequest(integrationVo, RequestFrom.MATRIX);
                if (StringUtils.isNotBlank(resultVo.getError())) {
                    logger.error(resultVo.getError());
                    throw new MatrixExternalAccessException();
                }
                resultList = matrixService.getExternalDataTbodyList(resultVo, columnList);
            }
            //去重
            String firstColumn = columnList.get(0);
            String secondColumn = columnList.get(0);
            if (columnList.size() >= 2) {
                secondColumn = columnList.get(1);
            }
            List<String> exsited = new ArrayList<>();
            Iterator<Map<String, JSONObject>> iterator = resultList.iterator();
            while (iterator.hasNext()) {
                Map<String, JSONObject> resultObj = iterator.next();
                JSONObject firstObj = resultObj.get(firstColumn);
                JSONObject secondObj = resultObj.get(secondColumn);
                String firstValue = firstObj.getString("value");
                String secondText = secondObj.getString("text");
                String compose = firstValue + SELECT_COMPOSE_JOINER + secondText;
                if (exsited.contains(compose)) {
                    iterator.remove();
                } else {
                    exsited.add(compose);
                }
            }
        }
        return resultList;
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
}
