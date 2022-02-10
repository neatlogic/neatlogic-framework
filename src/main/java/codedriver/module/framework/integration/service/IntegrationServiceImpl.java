/*
 * Copyright(c) 2022 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.integration.service;

import codedriver.framework.exception.integration.*;
import codedriver.framework.exception.type.ParamIrregularException;
import codedriver.framework.integration.core.IIntegrationHandler;
import codedriver.framework.integration.core.IntegrationHandlerFactory;
import codedriver.framework.integration.crossover.IntegrationCrossoverService;
import codedriver.framework.integration.dao.mapper.IntegrationMapper;
import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.integration.dto.table.ColumnVo;
import codedriver.framework.integration.dto.table.SourceColumnVo;
import codedriver.framework.util.javascript.JavascriptUtil;
import codedriver.module.framework.integration.handler.FrameworkRequestFrom;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author linbq
 * @since 2022/1/18 15:32
 **/
@Service
public class IntegrationServiceImpl implements IntegrationService, IntegrationCrossoverService {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationServiceImpl.class);

    @Resource
    private IntegrationMapper integrationMapper;
    @Override
    public List<ColumnVo> getColumnList(IntegrationVo integrationVo) {
        List<ColumnVo> resultList = new ArrayList<>();
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
                                    ColumnVo columnVo = new ColumnVo();
                                    columnVo.setUuid(theadObj.getString("key"));
                                    columnVo.setName(theadObj.getString("title"));
                                    columnVo.setType(theadObj.getString("type"));
                                    columnVo.setPrimaryKey(theadObj.getInteger("primaryKey"));
                                    Integer isSearchable = theadObj.getInteger("isSearchable");
                                    isSearchable = (isSearchable == null || isSearchable.intValue() != 1) ? 0 : 1;
                                    columnVo.setIsSearchable(isSearchable);
                                    Integer isSearch = theadObj.getInteger("isSearch");
                                    isSearch = (isSearch == null || isSearch.intValue() != 1) ? 0 : 1;
                                    columnVo.setIsSearch(isSearch);
                                    Boolean isPC = theadObj.getBoolean("isPC");
                                    isPC = isPC == null ? true : isPC;
                                    columnVo.setIsPC(isPC);
                                    Boolean isMobile = theadObj.getBoolean("isMobile");
                                    isMobile = isMobile == null ? true : isMobile;
                                    columnVo.setIsMobile(isMobile);
                                    columnVo.setSort(i);
                                    columnVo.setIsRequired(0);
                                    resultList.add(columnVo);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public JSONArray getTheadList(IntegrationVo integrationVo, List<String> columnList) {
        JSONArray resultList = new JSONArray();
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
                                if (CollectionUtils.isEmpty(columnList)) {
                                    return theadList;
                                }
                                Map<String, JSONObject> theadMap = new HashMap<>();
                                for (int i = 0; i < theadList.size(); i++) {
                                    JSONObject theadObj = theadList.getJSONObject(i);
                                    String key = theadObj.getString("key");
                                    theadMap.put(key, theadObj);
                                }
                                for (String column : columnList) {
                                    JSONObject theadObj = theadMap.get(column);
                                    if (theadObj != null) {
                                        resultList.add(theadObj);
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public List<Map<String, Object>> getTbodyList(IntegrationResultVo resultVo, List<String> columnList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (resultVo != null && StringUtils.isNotBlank(resultVo.getTransformedResult())) {
            JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
            if (MapUtils.isNotEmpty(transformedResult)) {
                JSONArray tbodyList = transformedResult.getJSONArray("tbodyList");
                if (CollectionUtils.isNotEmpty(tbodyList)) {
                    for (int i = 0; i < tbodyList.size(); i++) {
                        JSONObject rowData = tbodyList.getJSONObject(i);
                        Map<String, Object> resultMap = new HashMap<>(columnList.size());
                        for (String column : columnList) {
                            resultMap.put(column, rowData.get(column));
//                            String columnValue = rowData.getString(column);
//                            resultMap.put(column, matrixAttributeValueHandle(columnValue));
                        }
                        resultList.add(resultMap);
                    }
                }
            }
        }
        return resultList;
    }

//    @Override
//    public void arrayColumnDataConversion(List<String> arrayColumnList, List<Map<String, Object>> tbodyList) {
//        for (Map<String, Object> rowData : tbodyList) {
//            for (Map.Entry<String, Object> entry : rowData.entrySet()) {
//                if (arrayColumnList.contains(entry.getKey())) {
//                    List<ValueTextVo> valueObjList = new ArrayList<>();
//                    Object valueObj = entry.getValue();
//                    String value = valueObj.getString("value");
//                    if (StringUtils.isNotBlank(value)) {
//                        if (value.startsWith("[") && value.endsWith("]")) {
//                            List<String> valueList = valueObj.getJSONArray("value").toJavaList(String.class);
//                            for (String valueStr : valueList) {
//                                valueObjList.add(new ValueTextVo(valueStr, valueStr));
//                            }
//                        } else {
//                            valueObjList.add(new ValueTextVo(value, value));
//                        }
//                    }
//                    valueObj.put("value", valueObjList);
//                }
//            }
//        }
//    }

    /**
     * 集成属性数据查询
     * @param jsonObj 参数
     * @return
     */
    @Override
    public JSONObject searchTableData(JSONObject jsonObj) {
        JSONObject returnObj = new JSONObject();
        JSONArray columnArray = jsonObj.getJSONArray("columnList");
        if (CollectionUtils.isEmpty(columnArray)) {
            throw new ParamIrregularException("columnList");
        }
        String integrationUuid = jsonObj.getString("integrationUuid");
        IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(integrationUuid);
        if (integrationVo == null) {
            throw new IntegrationNotFoundException(integrationUuid);
        }
        IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(integrationVo.getHandler());
        if (handler == null) {
            throw new IntegrationHandlerNotFoundException(integrationVo.getHandler());
        }
        List<ColumnVo> columnVoList = getColumnList(integrationVo);
        if (CollectionUtils.isNotEmpty(columnVoList)) {
            List<String> columnList = columnArray.toJavaList(String.class);
            JSONArray theadList = getTheadList(integrationVo, columnList);
            returnObj.put("theadList", theadList);
            integrationVo.getParamObj().putAll(jsonObj);
            JSONArray defaultValue = jsonObj.getJSONArray("defaultValue");
            if (CollectionUtils.isNotEmpty(defaultValue)) {
                String uuidColumn = null;
                for (int i = 0; i < theadList.size(); i++) {
                    JSONObject theadObj = theadList.getJSONObject(i);
                    Integer primaryKey = theadObj.getInteger("primaryKey");
                    if (Objects.equals(primaryKey, 1)) {
                        uuidColumn = theadObj.getString("key");
                        break;
                    }
                }
                if (uuidColumn == null) {
                    throw new IntegrationTablePrimaryKeyColumnNotFoundException(integrationVo.getName());
                }
                List<SourceColumnVo> sourceColumnList = new ArrayList<>();
                SourceColumnVo sourceColumnVo = new SourceColumnVo();
                sourceColumnVo.setColumn(uuidColumn);
                List<Map<String, Object>> tbodyArray = new ArrayList<>();
                for (Object uuidValue : defaultValue) {
                    sourceColumnVo.setValue(uuidValue);
                    sourceColumnList.clear();
                    sourceColumnList.add(sourceColumnVo);
                    integrationVo.getParamObj().put("sourceColumnList", sourceColumnList);
                    IntegrationResultVo resultVo = handler.sendRequest(integrationVo, FrameworkRequestFrom.FORM);
                    if (StringUtils.isNotBlank(resultVo.getError())) {
                        logger.error(resultVo.getError());
                        throw new IntegrationSendRequestException(integrationVo.getName());
                    }
                    handler.validate(resultVo);
                    List<Map<String, Object>> tbodyList = getTbodyList(resultVo, columnList);
                    for (Map<String, Object> tbodyObj : tbodyList) {
                        if (Objects.equals(uuidValue, tbodyObj.get(uuidColumn))) {
                            tbodyArray.add(tbodyObj);
                            break;
                        }
                    }
                }
                returnObj.put("tbodyList", tbodyArray);
            } else {
                IntegrationResultVo resultVo = handler.sendRequest(integrationVo, FrameworkRequestFrom.FORM);
                if (StringUtils.isNotBlank(resultVo.getError())) {
                    logger.error(resultVo.getError());
                    throw new IntegrationSendRequestException(integrationVo.getName());
                }
                handler.validate(resultVo);
                JSONObject transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
                returnObj.put("currentPage", transformedResult.get("currentPage"));
                returnObj.put("pageSize", transformedResult.get("pageSize"));
                returnObj.put("pageCount", transformedResult.get("pageCount"));
                returnObj.put("rowNum", transformedResult.get("rowNum"));
                List<Map<String, Object>> tbodyList = getTbodyList(resultVo, columnList);
                returnObj.put("tbodyList", tbodyList);
                JSONArray searchColumnArray = jsonObj.getJSONArray("searchColumnList");
                if (CollectionUtils.isNotEmpty(searchColumnArray)) {
                    returnObj.put("searchColumnDetailList", getSearchColumnDetailList(integrationVo.getName(), columnVoList, searchColumnArray));
                }
            }
        }
        return returnObj;
    }

    private List<ColumnVo> getSearchColumnDetailList(String integrationUuid, List<ColumnVo> columnVoList, JSONArray searchColumnArray) {
        Map<String, ColumnVo> columnVoMap = new HashMap<>();
        for (ColumnVo columnVo : columnVoList) {
            columnVoMap.put(columnVo.getUuid(), columnVo);
        }
        List<ColumnVo> searchColumnDetailList = new ArrayList<>();
        List<String> searchColumnList = searchColumnArray.toJavaList(String.class);
        for (String column : searchColumnList) {
            ColumnVo columnVo = columnVoMap.get(column);
            if (columnVo == null) {
                throw new IntegrationTableColumnNotFoundException(integrationUuid, column);
            }
            searchColumnDetailList.add(columnVo);
        }
        return searchColumnDetailList;
    }
}
