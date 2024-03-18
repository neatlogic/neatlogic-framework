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

package neatlogic.module.framework.integration.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.exception.integration.*;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.integration.crossover.IntegrationCrossoverService;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.table.ColumnVo;
import neatlogic.framework.integration.dto.table.SourceColumnVo;
import neatlogic.framework.util.javascript.JavascriptUtil;
import neatlogic.module.framework.integration.handler.FrameworkRequestFrom;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
                                    columnVo.setUrlAttributeValue(theadObj.getString("urlAttributeValue"));
                                    columnVo.setPrimaryKey(theadObj.getInteger("primaryKey"));
                                    Integer isSearchable = theadObj.getInteger("isSearchable");
                                    isSearchable = (isSearchable == null || isSearchable.intValue() != 1) ? 0 : 1;
                                    columnVo.setIsSearchable(isSearchable);
                                    Integer isSearch = theadObj.getInteger("isSearch");
                                    isSearch = (isSearch == null || isSearch.intValue() != 1) ? 0 : 1;
                                    columnVo.setIsSearch(isSearch);
                                    Boolean isPC = theadObj.getBoolean("isPC");
                                    isPC = isPC == null || isPC;
                                    columnVo.setIsPC(isPC);
                                    Boolean isMobile = theadObj.getBoolean("isMobile");
                                    isMobile = isMobile != null && isMobile;
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

            String uuidColumn = null;
            for (ColumnVo columnVo : columnVoList) {
                Integer primaryKey = columnVo.getPrimaryKey();
                if (Objects.equals(primaryKey, 1)) {
                    uuidColumn = columnVo.getUuid();
                    break;
                }
            }
            if (uuidColumn == null) {
                throw new IntegrationTablePrimaryKeyColumnNotFoundException(integrationVo.getName());
            }
            List<String> columnList = columnArray.toJavaList(String.class);
            JSONArray theadList = getTheadList(integrationVo, columnList);
            returnObj.put("theadList", theadList);
            if (!columnList.contains(uuidColumn)) {
                columnList.add(uuidColumn);
            }
            JSONArray defaultValue = jsonObj.getJSONArray("defaultValue");
            if (CollectionUtils.isNotEmpty(defaultValue)) {
//                List<SourceColumnVo> sourceColumnList = new ArrayList<>();
//                SourceColumnVo sourceColumnVo = new SourceColumnVo();
//                sourceColumnVo.setColumn(uuidColumn);
                List<Map<String, Object>> tbodyArray = new ArrayList<>();
//                for (Object uuidValue : defaultValue) {
//                    List<String> valueList = new ArrayList<>();
//                    valueList.add(uuidValue.toString());
//                    sourceColumnVo.setValueList(valueList);
//                    sourceColumnList.clear();
//                    sourceColumnList.add(sourceColumnVo);
//                    integrationVo.getParamObj().put("sourceColumnList", sourceColumnList);
//                    IntegrationResultVo resultVo = handler.sendRequest(integrationVo, FrameworkRequestFrom.FORM);
//                    if (StringUtils.isNotBlank(resultVo.getError())) {
//                        logger.error(resultVo.getError());
//                        throw new IntegrationSendRequestException(integrationVo.getName());
//                    }
//                    handler.validate(resultVo);
//                    List<Map<String, Object>> tbodyList = getTbodyList(resultVo, columnList);
//                    for (Map<String, Object> tbodyObj : tbodyList) {
//                        if (Objects.equals(uuidValue, tbodyObj.get(uuidColumn))) {
//                            tbodyArray.add(tbodyObj);
//                            break;
//                        }
//                    }
//                }
                integrationVo.getParamObj().put("defaultValue", defaultValue);
                IntegrationResultVo resultVo = handler.sendRequest(integrationVo, FrameworkRequestFrom.FORM);
                if (StringUtils.isNotBlank(resultVo.getError())) {
                    logger.error(resultVo.getError());
                    throw new IntegrationSendRequestException(integrationVo.getName());
                }
                handler.validate(resultVo);
                List<Map<String, Object>> tbodyList = getTbodyList(resultVo, columnList);
                tbodyArray.addAll(tbodyList);
                returnObj.put("tbodyList", tbodyArray);
            } else {
                JSONArray searchColumnArray = jsonObj.getJSONArray("searchColumnList");
                if (CollectionUtils.isNotEmpty(searchColumnArray)) {
                    returnObj.put("searchColumnDetailList", getSearchColumnDetailList(integrationVo.getName(), columnVoList, searchColumnArray));
                }
                List<SourceColumnVo> sourceColumnList = new ArrayList<>();
                JSONArray sourceColumnArray = jsonObj.getJSONArray("sourceColumnList");
                if (CollectionUtils.isNotEmpty(sourceColumnArray)) {
                    sourceColumnList = sourceColumnArray.toJavaList(SourceColumnVo.class);
                    Iterator<SourceColumnVo> iterator = sourceColumnList.iterator();
                    while (iterator.hasNext()) {
                        SourceColumnVo sourceColumnVo = iterator.next();
                        if (StringUtils.isBlank(sourceColumnVo.getColumn())) {
                            iterator.remove();
                        } else if (CollectionUtils.isEmpty(sourceColumnVo.getValueList())) {
                            iterator.remove();
                        }
                    }
                }
                JSONArray filterList = jsonObj.getJSONArray("filterList");
                if (CollectionUtils.isNotEmpty(filterList)) {
                    if (!mergeFilterListAndSourceColumnList(filterList, sourceColumnList)) {
                        return returnObj;
                    }
                }
                JSONObject paramObj = new JSONObject();
                paramObj.put("currentPage", jsonObj.getInteger("currentPage"));
                paramObj.put("pageSize", jsonObj.getInteger("pageSize"));
                paramObj.put("needPage", jsonObj.getBoolean("needPage"));
                paramObj.put("sourceColumnList", sourceColumnList);
                integrationVo.setParamObj(paramObj);
//                integrationVo.getParamObj().putAll(jsonObj);
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
            }
        }
        return returnObj;
    }

    @Override
    public boolean mergeFilterListAndSourceColumnList(JSONArray filterList, List<SourceColumnVo> sourceColumnList) {
        Map<String, SourceColumnVo> sourceColumnMap = sourceColumnList.stream().collect(Collectors.toMap(e -> e.getColumn(), e -> e));
        for (int i = 0; i < filterList.size(); i++) {
            JSONObject filterObj = filterList.getJSONObject(i);
            if (MapUtils.isEmpty(filterObj)) {
                continue;
            }
            String uuid = filterObj.getString("uuid");
            if (StringUtils.isBlank(uuid)) {
                continue;
            }
            JSONArray valueArray = filterObj.getJSONArray("valueList");
            if (CollectionUtils.isEmpty(valueArray)) {
                continue;
            }
            List<String> filterValueList = valueArray.toJavaList(String.class);
            SourceColumnVo sourceColumnVo = sourceColumnMap.get(uuid);
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
            SourceColumnVo sourceColumn = new SourceColumnVo();
            sourceColumn.setColumn(uuid);
            sourceColumn.setExpression(Expression.INCLUDE.getExpression());
            sourceColumn.setValueList(filterValueList);
            sourceColumnList.add(sourceColumn);
        }
        return true;
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
