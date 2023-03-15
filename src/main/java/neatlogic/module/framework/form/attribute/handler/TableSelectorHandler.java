/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.form.attribute.handler;

import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.form.attribute.core.FormAttributeDataConversionHandlerFactory;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.attribute.core.IFormAttributeDataConversionHandler;
import neatlogic.framework.form.attribute.core.IFormAttributeHandler;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.matrix.core.IMatrixDataSourceHandler;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerFactory;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import neatlogic.framework.matrix.dto.MatrixVo;
import neatlogic.framework.matrix.exception.MatrixDataSourceHandlerNotFoundException;
import neatlogic.framework.matrix.exception.MatrixNotFoundException;
import neatlogic.framework.util.TableResultUtil;
import neatlogic.module.framework.integration.service.IntegrationService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class TableSelectorHandler extends FormHandlerBase {

    private final static Logger logger = LoggerFactory.getLogger(TableSelectorHandler.class);

    @Resource
    private IntegrationService integrationService;

    @Override
    public String getHandler() {
        return FormHandler.FORMTABLESELECTOR.getHandler();
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        if (!attributeDataVo.dataIsEmpty()) {
            return "已更新";
        } else {
            return "";
        }
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject tableObj = new JSONObject();
        // TODO 由于表单重构，数据结构已发生变化，暂时屏蔽些代码使得工单处理触发邮件时不报错，已建需求【ID1007302】重构表单邮件模板及取数逻辑，后面统一测试表单所有组件的邮件模板显示
//        JSONObject dataObj = (JSONObject) attributeDataVo.getDataObj();
//        if (MapUtils.isEmpty(dataObj)) {
//            return tableObj;
//        }
//        JSONArray selectUuidList = dataObj.getJSONArray("selectUuidList");
//        if (CollectionUtils.isEmpty(selectUuidList)) {
//            return tableObj;
//        }
//        String mode = configObj.getString("mode");
//        Boolean needPage = configObj.getBoolean("needPage");
//        String dataSource = configObj.getString("dataSource");
//        if ("matrix".equals(dataSource)) {
//            if ("normal".equals(mode) && Objects.equals(needPage, false)) {//不分页
//                tableObj = matrixDataSourceNoNeedPage(dataObj, selectUuidList, configObj);
//            } else {//分页
//                tableObj = matrixDataSourceNeedPage(dataObj, selectUuidList, configObj);
//            }
//        } else if ("integration".equals(dataSource)) {
//            if ("normal".equals(mode) && Objects.equals(needPage, false)) {//不分页
//                tableObj = integrationDataSourceNoNeedPage(dataObj, selectUuidList, configObj);
//            } else {//分页
//                tableObj = integrationDataSourceNeedPage(dataObj, selectUuidList, configObj);
//            }
//        }
//        if (MapUtils.isNotEmpty(tableObj)) {
//            JSONArray theadList = tableObj.getJSONArray("theadList");
//            JSONArray tbodyList = tableObj.getJSONArray("tbodyList");
//            List<String> keyList = new ArrayList<>();
//            for (int i = 0; i < theadList.size(); i++) {
//                JSONObject theadObj = theadList.getJSONObject(i);
//                keyList.add(theadObj.getString("key"));
//            }
//            if (CollectionUtils.isNotEmpty(tbodyList)) {
//                for (int i = 0; i < tbodyList.size(); i++) {
//                    JSONObject tbodyObj = tbodyList.getJSONObject(i);
//                    for (String key : keyList) {
//                        if (!tbodyObj.containsKey(key)) {
//                            tbodyObj.put(key, new JSONObject());
//                        }
//                    }
//                }
//            }
//        }
        return tableObj;
    }

    private JSONObject matrixDataSourceNeedPage(JSONObject dataObj, JSONArray selectUuidList, JSONObject configObj) {
        JSONObject tableObj = new JSONObject();
        List<String> columnList = new ArrayList<>();
        JSONArray columnHeadList = configObj.getJSONArray("dataConfig");
        if (CollectionUtils.isNotEmpty(columnHeadList)) {
            for (int i = 0; i < columnHeadList.size(); i++) {
                JSONObject columnHeadObj = columnHeadList.getJSONObject(i);
//                Boolean isPC = columnHeadObj.getBoolean("isPC");
//                if (Objects.equals(isPC, false)) {
//                    continue;
//                }
                String uuid = columnHeadObj.getString("uuid");
                if (StringUtils.isNotBlank(uuid)) {
                    columnList.add(uuid);
                }
            }
        }
        String matrixUuid = configObj.getString("matrixUuid");
        MatrixVo matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
        if (matrixVo == null) {
            throw new MatrixNotFoundException(matrixUuid);
        }
        IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrixVo.getType());
        if (matrixDataSourceHandler == null) {
            throw new MatrixDataSourceHandlerNotFoundException(matrixVo.getType());
        }
        MatrixDataVo dataVo = new MatrixDataVo();
        dataVo.setMatrixUuid(matrixUuid);
        dataVo.setColumnList(columnList);
        dataVo.setDefaultValue(selectUuidList);
        String uuidColumn = configObj.getString("uuidColumn");
        uuidColumn = uuidColumn == null ? "uuid" : uuidColumn;
        dataVo.setUuidColumn(uuidColumn);
        dataVo.setNeedPage(false);
        JSONObject resultObj = matrixDataSourceHandler.searchTableData(dataVo);
        if (MapUtils.isNotEmpty(resultObj)) {
            JSONArray theadList = resultObj.getJSONArray("theadList");
            JSONArray tbodyList = resultObj.getJSONArray("tbodyList");
            if (CollectionUtils.isNotEmpty(tbodyList)) {
                for (int i = 0; i < tbodyList.size(); i++) {
                    JSONObject tbodyObj = tbodyList.getJSONObject(i);
                    tbodyObj.put("_isSelected", true);
                }
            }
            Map<String, String> secondEditColumnMap = new HashMap<>();
            JSONArray dataConfig = configObj.getJSONArray("dataConfig");
            for (int i = 0; i < dataConfig.size(); i++) {
                JSONObject dataConfigObj = dataConfig.getJSONObject(i);
                Boolean isEdit = dataConfigObj.getBoolean("isEdit");
                if (Objects.equals(isEdit, true)) {
                    String secondEditColumn = dataConfigObj.getString("uuid");
                    if (StringUtils.isNotBlank(secondEditColumn)) {
                        secondEditColumnMap.put(secondEditColumn, "value");
                    }
                }
            }
            JSONObject extendedDataObj = dataObj.getJSONObject("extendedData");
            if (MapUtils.isNotEmpty(secondEditColumnMap)) {
                setSecondEditAttributeData(tbodyList, uuidColumn, secondEditColumnMap, extendedDataObj);
            }
            JSONArray attributeList = configObj.getJSONArray("attributeList");
            if (CollectionUtils.isNotEmpty(attributeList)) {
                setExtAttributeData(theadList, tbodyList, configObj, extendedDataObj);
            }
            tableObj.put("selectUuidList", selectUuidList);
            tableObj.put("theadList", theadList);
            tableObj.put("tbodyList", tbodyList);
        }
        return tableObj;
    }

    private JSONObject matrixDataSourceNoNeedPage(JSONObject dataObj, JSONArray selectUuidList, JSONObject configObj) {
        JSONObject tableObj = new JSONObject();
        tableObj.put("selectUuidList", selectUuidList);
        JSONObject table = dataObj.getJSONObject("table");
        if (MapUtils.isNotEmpty(table)) {
            JSONArray theadList = table.getJSONArray("theadList");
            JSONArray tbodyList = table.getJSONArray("tbodyList");
            String uuidColumn = configObj.getString("uuidColumn");
            if (StringUtils.isBlank(uuidColumn)) {
                uuidColumn = "uuid";
            }
            Map<String, String> secondEditColumnMap = new HashMap<>();
            JSONArray dataConfig = configObj.getJSONArray("dataConfig");
            for (int i = 0; i < dataConfig.size(); i++) {
                JSONObject dataConfigObj = dataConfig.getJSONObject(i);
                Boolean isEdit = dataConfigObj.getBoolean("isEdit");
                if (Objects.equals(isEdit, true)) {
                    String secondEditColumn = dataConfigObj.getString("uuid");
                    if (StringUtils.isNotBlank(secondEditColumn)) {
                        secondEditColumnMap.put(secondEditColumn, "value");
                    }
                }
            }
            JSONObject extendedDataObj = dataObj.getJSONObject("extendedData");
            if (MapUtils.isNotEmpty(secondEditColumnMap)) {
                setSecondEditAttributeData(tbodyList, uuidColumn, secondEditColumnMap, extendedDataObj);
            }
            JSONArray attributeList = configObj.getJSONArray("attributeList");
            if (CollectionUtils.isNotEmpty(attributeList)) {
                setExtAttributeData(theadList, tbodyList, configObj, extendedDataObj);
            }
            tableObj.put("theadList", theadList);
            tableObj.put("tbodyList", tbodyList);
        }
        return tableObj;
    }

    private JSONObject integrationDataSourceNeedPage(JSONObject dataObj, JSONArray selectUuidList, JSONObject configObj) {
        JSONObject tableObj = new JSONObject();
        List<String> columnList = new ArrayList<>();
        JSONArray columnHeadList = configObj.getJSONArray("dataConfig");
        if (CollectionUtils.isNotEmpty(columnHeadList)) {
            for (int i = 0; i < columnHeadList.size(); i++) {
                JSONObject columnHeadObj = columnHeadList.getJSONObject(i);
//                Boolean isPC = columnHeadObj.getBoolean("isPC");
//                if (Objects.equals(isPC, false)) {
//                    continue;
//                }
                String uuid = columnHeadObj.getString("uuid");
                if (StringUtils.isNotBlank(uuid)) {
                    columnList.add(uuid);
                }
            }
        }
        String integrationUuid = configObj.getString("integrationUuid");
        String uuidColumn = configObj.getString("uuidColumn");
        uuidColumn = uuidColumn == null ? "uuid" : uuidColumn;
        JSONObject paramObj = new JSONObject();
        paramObj.put("integrationUuid", integrationUuid);
        paramObj.put("columnList", columnList);
        paramObj.put("defaultValue", selectUuidList);
        paramObj.put("uuidColumn", uuidColumn);
        paramObj.put("needPage", false);
        JSONObject resultObj = integrationService.searchTableData(paramObj);
        if (MapUtils.isNotEmpty(resultObj)) {
            Map<String, JSONObject> secondEditColumnMap = new HashMap<>();
            List<String> theadKeyList = new ArrayList<>();
            JSONArray theadList = new JSONArray();
            JSONArray theadArray = resultObj.getJSONArray("theadList");
            if (CollectionUtils.isNotEmpty(theadArray)) {
                for (int i = 0; i < theadArray.size(); i++) {
                    JSONObject theadObj = theadArray.getJSONObject(i);
                    String key = theadObj.getString("key");
                    theadKeyList.add(key);
                    Integer primaryKey = theadObj.getInteger("primaryKey");
                    if (Objects.equals(primaryKey, 1)) {
                        uuidColumn = key;
                    }
                    JSONObject config = theadObj.getJSONObject("config");
                    if (MapUtils.isNotEmpty(config)) {
                        secondEditColumnMap.put(key, config);
                    }
                    JSONObject newTheadObj = new JSONObject();
                    newTheadObj.put("key", key);
                    newTheadObj.put("title", theadObj.getString("title"));
                    theadList.add(newTheadObj);
                }
            }
            JSONArray tbodyList = new JSONArray();
            JSONArray tbodyArray = resultObj.getJSONArray("tbodyList");
            if (CollectionUtils.isNotEmpty(tbodyArray)) {
                for (int i = 0; i < tbodyArray.size(); i++) {
                    List<String> tbodyKeyList = new ArrayList<>();
                    JSONObject newTbodyObj = new JSONObject();
                    JSONObject tbodyObj = tbodyArray.getJSONObject(i);
                    newTbodyObj.put("_isSelected", true);
                    for (Map.Entry<String, Object> entry : tbodyObj.entrySet()) {
                        String key = entry.getKey();
                        tbodyKeyList.add(key);
                        Object value = entry.getValue();
                        if ("_isSelected".equals(key)) {
                            newTbodyObj.put(key, value);
                        } else {
                            if (value == null) {
                                value = "";
                            }
                            JSONObject valueOjb = new JSONObject();
                            valueOjb.put("value", value);
                            valueOjb.put("text", value);
                            valueOjb.put("type", "input");
                            newTbodyObj.put(key, valueOjb);
                        }
                    }
                    List<String> keyList = ListUtils.removeAll(theadKeyList, tbodyKeyList);
                    if (CollectionUtils.isNotEmpty(keyList)) {
                        for (String key : keyList) {
                            JSONObject valueOjb = new JSONObject();
                            valueOjb.put("value", "");
                            valueOjb.put("text", "");
                            valueOjb.put("type", "input");
                            newTbodyObj.put(key, valueOjb);
                        }
                    }
                    tbodyList.add(newTbodyObj);
                }
            }
            JSONObject extendedDataObj = dataObj.getJSONObject("extendedData");
            if (MapUtils.isNotEmpty(secondEditColumnMap)) {
                setIntegrationSecondEditAttributeData(tbodyList, uuidColumn, secondEditColumnMap, extendedDataObj);
            }
            JSONArray attributeList = configObj.getJSONArray("attributeList");
            if (CollectionUtils.isNotEmpty(attributeList)) {
                setExtAttributeData(theadList, tbodyList, configObj, extendedDataObj);
            }
            tableObj.put("selectUuidList", selectUuidList);
            tableObj.put("theadList", theadList);
            tableObj.put("tbodyList", tbodyList);
        }
        return tableObj;
    }

    private JSONObject integrationDataSourceNoNeedPage(JSONObject dataObj, JSONArray selectUuidList, JSONObject configObj) {
        JSONObject tableObj = new JSONObject();
        tableObj.put("selectUuidList", selectUuidList);
        JSONObject table = dataObj.getJSONObject("table");
        if (MapUtils.isNotEmpty(table)) {
            String uuidColumn = "uuid";
            Map<String, JSONObject> secondEditColumnMap = new HashMap<>();
            List<String> theadKeyList = new ArrayList<>();
            JSONArray theadList = new JSONArray();
            JSONArray theadArray = table.getJSONArray("theadList");
            if (CollectionUtils.isNotEmpty(theadArray)) {
                for (int i = 0; i < theadArray.size(); i++) {
                    JSONObject theadObj = theadArray.getJSONObject(i);
                    String key = theadObj.getString("key");
                    theadKeyList.add(key);
                    Integer primaryKey = theadObj.getInteger("primaryKey");
                    if (Objects.equals(primaryKey, 1)) {
                        uuidColumn = key;
                    }
                    JSONObject config = theadObj.getJSONObject("config");
                    if (MapUtils.isNotEmpty(config)) {
                        secondEditColumnMap.put(key, config);
                    }
                    JSONObject newTheadObj = new JSONObject();
                    newTheadObj.put("key", key);
                    newTheadObj.put("title", theadObj.getString("title"));
                    theadList.add(newTheadObj);
                }
            }
            JSONArray tbodyList = new JSONArray();
            JSONArray tbodyArray = table.getJSONArray("tbodyList");
            if (CollectionUtils.isNotEmpty(tbodyArray)) {
                for (int i = 0; i < tbodyArray.size(); i++) {
                    List<String> tbodyKeyList = new ArrayList<>();
                    JSONObject newTbodyObj = new JSONObject();
                    JSONObject tbodyObj = tbodyArray.getJSONObject(i);
                    for (Map.Entry<String, Object> entry : tbodyObj.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        tbodyKeyList.add(key);
                        if ("_isSelected".equals(key)) {
                            newTbodyObj.put(key, value);
                        } else {
                            if (value == null) {
                                value = "";
                            }
                            JSONObject valueOjb = new JSONObject();
                            valueOjb.put("value", value);
                            valueOjb.put("text", value);
                            valueOjb.put("type", "input");
                            newTbodyObj.put(key, valueOjb);
                        }
                    }
                    List<String> keyList = ListUtils.removeAll(theadKeyList, tbodyKeyList);
                    if (CollectionUtils.isNotEmpty(keyList)) {
                        for (String key : keyList) {
                            JSONObject valueOjb = new JSONObject();
                            valueOjb.put("value", "");
                            valueOjb.put("text", "");
                            valueOjb.put("type", "input");
                            newTbodyObj.put(key, valueOjb);
                        }
                    }
                    tbodyList.add(newTbodyObj);
                }
            }
            JSONObject extendedDataObj = dataObj.getJSONObject("extendedData");
            if (MapUtils.isNotEmpty(secondEditColumnMap)) {
                setIntegrationSecondEditAttributeData(tbodyList, uuidColumn, secondEditColumnMap, extendedDataObj);
            }
            JSONArray attributeList = configObj.getJSONArray("attributeList");
            if (CollectionUtils.isNotEmpty(attributeList)) {
                setExtAttributeData(theadList, tbodyList, configObj, extendedDataObj);
            }
            tableObj.put("theadList", theadList);
            tableObj.put("tbodyList", tbodyList);
        }
        return tableObj;
    }

    /**
     * 处理二次编辑数据
     *
     * @param tbodyList
     * @param uuidColumn
     * @param secondEditColumnMap
     * @param extendedDataObj
     */
    private void setSecondEditAttributeData(JSONArray tbodyList, String uuidColumn, Map<String, String> secondEditColumnMap, JSONObject extendedDataObj) {
        for (int i = 0; i < tbodyList.size(); i++) {
            JSONObject tbodyObj = tbodyList.getJSONObject(i);
            JSONObject uuidColumnObj = tbodyObj.getJSONObject(uuidColumn);
            String uuidValue = uuidColumnObj.getString("value");

            for (Map.Entry<String, String> entry : secondEditColumnMap.entrySet()) {
                String column = entry.getKey();
                String valueName = entry.getValue();
                JSONObject columnDataObj = tbodyObj.getJSONObject(column);
                if (MapUtils.isEmpty(columnDataObj)) {
                    continue;
                }
                JSONArray columnValueArray = columnDataObj.getJSONArray("value");
                if (CollectionUtils.isEmpty(columnValueArray)) {
                    continue;
                }
                JSONArray newColumnValueArray = new JSONArray();
                if (MapUtils.isNotEmpty(extendedDataObj)) {
                    JSONObject extendedRowDataObj = extendedDataObj.getJSONObject(uuidValue);
                    if (MapUtils.isNotEmpty(extendedRowDataObj)) {
                        Object selectedValueObj = extendedRowDataObj.get(column);
                        if (selectedValueObj instanceof JSONArray) {
                            JSONArray selectedValueArray = (JSONArray) selectedValueObj;
                            if (CollectionUtils.isNotEmpty(selectedValueArray)) {
                                for (int j = 0; j < columnValueArray.size(); j++) {
                                    JSONObject columnValueObj = columnValueArray.getJSONObject(j);
                                    Object value = columnValueObj.get(valueName);
                                    if (selectedValueArray.contains(value)) {
                                        newColumnValueArray.add(columnValueObj);
                                    }
                                }
                            }
                        } else {
                            for (int j = 0; j < columnValueArray.size(); j++) {
                                Object columnValue = columnValueArray.get(j);
                                if (columnValue instanceof JSONObject) {
                                    JSONObject columnValueObj = (JSONObject) columnValue;
                                    Object value = columnValueObj.get(valueName);
                                    if (Objects.equals(selectedValueObj, value)) {
                                        newColumnValueArray.add(columnValueObj);
                                    }
                                } else {
                                    if (Objects.equals(selectedValueObj, columnValue)) {
                                        newColumnValueArray.add(columnValue);
                                    }
                                }
                            }
                        }
                    }
                }
                columnDataObj.put("value", newColumnValueArray);
                columnDataObj.put("type", "selects");
                List<String> textList = new ArrayList<>();
                for (int j = 0; j < newColumnValueArray.size(); j++) {
                    JSONObject columnValueObj = newColumnValueArray.getJSONObject(j);
                    String text = columnValueObj.getString("text");
                    if (StringUtils.isNotBlank(text)) {
                        textList.add(text);
                    }
                }
                columnDataObj.put("text", textList);
            }
        }
    }

    /**
     * 处理二次编辑数据
     *
     * @param tbodyList
     * @param uuidColumn
     * @param secondEditColumnMap
     * @param extendedDataObj
     */
    private void setIntegrationSecondEditAttributeData(JSONArray tbodyList, String uuidColumn, Map<String, JSONObject> secondEditColumnMap, JSONObject extendedDataObj) {
        for (int i = 0; i < tbodyList.size(); i++) {
            JSONObject tbodyObj = tbodyList.getJSONObject(i);
            JSONObject uuidColumnObj = tbodyObj.getJSONObject(uuidColumn);
            String uuidValue = uuidColumnObj.getString("value");
            JSONObject extendedRowDataObj = extendedDataObj.getJSONObject(uuidValue);
            for (Map.Entry<String, JSONObject> entry : secondEditColumnMap.entrySet()) {
                String column = entry.getKey();
                JSONObject columnValueObj = null;
                JSONObject columnDataObj = tbodyObj.getJSONObject(column);
                if (MapUtils.isNotEmpty(columnDataObj)) {
                    columnValueObj = columnDataObj.getJSONObject("value");
                }
                JSONObject config = entry.getValue();
                String type = config.getString("type");
                if ("text".equals(type)) {
                    String valueStr = null;
                    if (MapUtils.isNotEmpty(extendedRowDataObj)) {
                        valueStr = extendedRowDataObj.getString(column);
                    }
                    if (valueStr == null) {
                        if (MapUtils.isNotEmpty(columnValueObj)) {
                            valueStr = columnValueObj.getString("value");
                        }
                    }
                    if (valueStr == null) {
                        valueStr = config.getString("value");
                    }
                    if (valueStr != null) {
                        JSONObject newColumnDataObj = new JSONObject();
                        newColumnDataObj.put("value", valueStr);
                        newColumnDataObj.put("type", "input");
                        newColumnDataObj.put("text", valueStr);
                        tbodyObj.put(column, newColumnDataObj);
                    } else {
                        JSONObject newColumnDataObj = new JSONObject();
                        newColumnDataObj.put("value", "");
                        newColumnDataObj.put("type", "input");
                        newColumnDataObj.put("text", "");
                        tbodyObj.put(column, newColumnDataObj);
                    }
                } else {
                    JSONArray dataList = null;
                    if (MapUtils.isNotEmpty(columnValueObj)) {
                        String rootName = config.getString("rootName");
                        if (StringUtils.isBlank(rootName)) {
                            rootName = "dataList";
                        }
                        dataList = columnValueObj.getJSONArray(rootName);
                    }
                    if (dataList == null) {
                        dataList = config.getJSONArray("dataList");
                        if (dataList == null) {
                            dataList = new JSONArray();
                        }
                    }
                    String valueName = config.getString("valueName");
                    if (StringUtils.isBlank(valueName)) {
                        valueName = "value";
                    }
                    String textName = config.getString("textName");
                    if (StringUtils.isBlank(textName)) {
                        textName = "text";
                    }
                    Boolean multiple = config.getBoolean("multiple");
                    if (multiple == null) {
                        if ("checkbox".equals(type)) {
                            multiple = true;
                        } else {
                            multiple = false;
                        }
                    }
                    if (multiple) {
                        JSONArray selectedValueArray = null;
                        if (MapUtils.isNotEmpty(extendedRowDataObj)) {
                            selectedValueArray = extendedRowDataObj.getJSONArray(column);
                        }
                        if (selectedValueArray == null) {
                            if (MapUtils.isNotEmpty(columnValueObj)) {
                                selectedValueArray = columnValueObj.getJSONArray("value");
                            }
                        }
                        if (selectedValueArray == null) {
                            selectedValueArray = config.getJSONArray("value");
                        }
                        if (CollectionUtils.isNotEmpty(selectedValueArray)) {
                            JSONArray newColumnValueArray = new JSONArray();
                            for (int j = 0; j < dataList.size(); j++) {
                                JSONObject dataObj = dataList.getJSONObject(j);
                                Object valueObj = dataObj.get(valueName);
                                if (selectedValueArray.contains(valueObj)) {
                                    newColumnValueArray.add(dataObj);
                                }
                            }
                            JSONObject newColumnDataObj = new JSONObject();
                            newColumnDataObj.put("value", newColumnValueArray);
                            newColumnDataObj.put("type", "selects");
                            List<String> textList = new ArrayList<>();
                            for (int j = 0; j < newColumnValueArray.size(); j++) {
                                JSONObject columnValueObject = newColumnValueArray.getJSONObject(j);
                                String text = columnValueObject.getString(textName);
                                if (StringUtils.isNotBlank(text)) {
                                    textList.add(text);
                                }
                            }
                            newColumnDataObj.put("text", textList);
                            tbodyObj.put(column, newColumnDataObj);
                        } else {
                            JSONObject newColumnDataObj = new JSONObject();
                            newColumnDataObj.put("value", "");
                            newColumnDataObj.put("type", "input");
                            newColumnDataObj.put("text", "");
                            tbodyObj.put(column, newColumnDataObj);
                        }
                    } else {
                        Object selectedValueObj = null;
                        if (MapUtils.isNotEmpty(extendedRowDataObj)) {
                            selectedValueObj = extendedRowDataObj.get(column);
                        }
                        if (selectedValueObj == null) {
                            if (MapUtils.isNotEmpty(columnValueObj)) {
                                selectedValueObj = columnValueObj.get("value");
                            }
                        }
                        if (selectedValueObj == null) {
                            selectedValueObj = config.get("value");
                        }
                        if (selectedValueObj != null) {
                            JSONObject newColumnDataObj = new JSONObject();
                            newColumnDataObj.put("type", "input");
                            for (int j = 0; j < dataList.size(); j++) {
                                JSONObject dataObj = dataList.getJSONObject(j);
                                Object valueObj = dataObj.get(valueName);
                                if (Objects.equals(selectedValueObj, valueObj)) {
                                    newColumnDataObj.put("value", dataObj.get(valueName));
                                    newColumnDataObj.put("text", dataObj.get(textName));
                                }
                            }
                            tbodyObj.put(column, newColumnDataObj);
                        } else {
                            JSONObject newColumnDataObj = new JSONObject();
                            newColumnDataObj.put("value", "");
                            newColumnDataObj.put("type", "input");
                            newColumnDataObj.put("text", "");
                            tbodyObj.put(column, newColumnDataObj);
                        }
                    }
                }
            }
        }
    }

    private void setExtAttributeData(JSONArray theadList, JSONArray tbodyList, JSONObject configObj, JSONObject dataObj) {
        JSONArray attributeList = configObj.getJSONArray("attributeList");
        if (CollectionUtils.isEmpty(attributeList)) {
            return;
        }
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < theadList.size(); i++) {
            JSONObject theadObj = theadList.getJSONObject(i);
            String key = theadObj.getString("key");
            if (StringUtils.isNotBlank(key)) {
                keyList.add(key);
            }
        }
        for (int i = 0; i < attributeList.size(); i++) {
            JSONObject attributeObj = attributeList.getJSONObject(i);
            String attributeUuid = attributeObj.getString("attributeUuid");
            String attribute = attributeObj.getString("attribute");
            JSONObject theadObj = new JSONObject();
            theadObj.put("title", attribute);
            theadObj.put("key", attributeUuid);
            if (!keyList.contains(attributeUuid)) {
                theadList.add(theadObj);
            }
        }
        String uuidColumn = configObj.getString("uuidColumn");
        uuidColumn = uuidColumn == null ? "uuid" : uuidColumn;
        for (int i = 0; i < tbodyList.size(); i++) {
            JSONObject tbodyObj = tbodyList.getJSONObject(i);
            JSONObject uuidColumnCellData = tbodyObj.getJSONObject(uuidColumn);
            String uuidColumnCellDataValue = uuidColumnCellData.getString("value");
            JSONObject extData = dataObj.getJSONObject(uuidColumnCellDataValue);
            for (int j = 0; j < attributeList.size(); j++) {
                JSONObject attributeObj = attributeList.getJSONObject(j);
                JSONObject cellObj = new JSONObject();
                String attributeUuid = attributeObj.getString("attributeUuid");
                if (MapUtils.isNotEmpty(extData)) {
                    String type = attributeObj.getString("type");
                    cellObj.put("type", type);
                    if ("text".equals(type)) {
                        String value = extData.getString(attributeUuid);
                        cellObj.put("value", value);
                        cellObj.put("text", value);
                    } else if ("textarea".equals(type)) {
                        String value = extData.getString(attributeUuid);
                        cellObj.put("value", value);
                        cellObj.put("text", value);
                    } else if ("select".equals(type)) {
                        String value = extData.getString(attributeUuid);
                        cellObj.put("value", value);
                        String text = "";
                        if (StringUtils.isNotBlank(value)) {
                            JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                            String dataSource = attrConfig.getString("dataSource");
                            if ("static".equals(dataSource)) {
                                JSONArray dataList = attrConfig.getJSONArray("dataList");
                                for (int k = 0; k < dataList.size(); k++) {
                                    JSONObject data = dataList.getJSONObject(k);
                                    String dataValue = data.getString("value");
                                    if (dataValue.equals(value)) {
                                        text = data.getString("text");
                                    }
                                }
                            } else if ("matrix".equals(dataSource)) {
                                String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
                                if (split.length == 2) {
                                    text = split[1];
                                } else {
                                    text = value;
                                }
                            }
                        }
                        cellObj.put("text", text);
                    } else if ("selects".equals(type)) {
                        JSONArray valueList = extData.getJSONArray(attributeUuid);
                        cellObj.put("value", valueList);
                        JSONArray textList = new JSONArray();
                        if (CollectionUtils.isNotEmpty(valueList)) {
                            for (String value : valueList.toJavaList(String.class)) {
                                String text = "";
                                if (StringUtils.isNotBlank(value)) {
                                    JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                                    String dataSource = attrConfig.getString("dataSource");
                                    if ("static".equals(dataSource)) {
                                        JSONArray dataList = attrConfig.getJSONArray("dataList");
                                        for (int k = 0; k < dataList.size(); k++) {
                                            JSONObject data = dataList.getJSONObject(k);
                                            String dataValue = data.getString("value");
                                            if (dataValue.equals(value)) {
                                                text = data.getString("text");
                                            }
                                        }
                                    } else if ("matrix".equals(dataSource)) {
                                        String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
                                        if (split.length == 2) {
                                            text = split[1];
                                        } else {
                                            text = value;
                                        }
                                    }
                                }
                                textList.add(text);
                            }
                        }
                        cellObj.put("text", textList);
                    } else if ("radio".equals(type)) {
                        String value = extData.getString(attributeUuid);
                        cellObj.put("value", value);
                        String text = "";
                        if (StringUtils.isNotBlank(value)) {
                            JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                            String dataSource = attrConfig.getString("dataSource");
                            if ("static".equals(dataSource)) {
                                JSONArray dataList = attrConfig.getJSONArray("dataList");
                                for (int k = 0; k < dataList.size(); k++) {
                                    JSONObject data = dataList.getJSONObject(k);
                                    String dataValue = data.getString("value");
                                    if (dataValue.equals(value)) {
                                        text = data.getString("text");
                                    }
                                }
                            } else if ("matrix".equals(dataSource)) {
                                String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
                                if (split.length == 2) {
                                    text = split[1];
                                } else {
                                    text = value;
                                }
                            }
                        }
                        cellObj.put("text", text);
                    } else if ("checkbox".equals(type)) {
                        JSONArray valueList = extData.getJSONArray(attributeUuid);
                        cellObj.put("value", valueList);
                        JSONArray textList = new JSONArray();
                        if (CollectionUtils.isNotEmpty(valueList)) {
                            for (String value : valueList.toJavaList(String.class)) {
                                String text = "";
                                if (StringUtils.isNotBlank(value)) {
                                    JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                                    String dataSource = attrConfig.getString("dataSource");
                                    if ("static".equals(dataSource)) {
                                        JSONArray dataList = attrConfig.getJSONArray("dataList");
                                        for (int k = 0; k < dataList.size(); k++) {
                                            JSONObject data = dataList.getJSONObject(k);
                                            String dataValue = data.getString("value");
                                            if (dataValue.equals(value)) {
                                                text = data.getString("text");
                                            }
                                        }
                                    } else if ("matrix".equals(dataSource)) {
                                        String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
                                        if (split.length == 2) {
                                            text = split[1];
                                        } else {
                                            text = value;
                                        }
                                    }
                                }
                                textList.add(text);
                            }
                        }
                        cellObj.put("text", textList);
                    } else if ("date".equals(type)) {
                        String value = extData.getString(attributeUuid);
                        cellObj.put("value", value);
                        cellObj.put("text", value);
                    } else if ("time".equals(type)) {
                        String value = extData.getString(attributeUuid);
                        cellObj.put("value", value);
                        cellObj.put("text", value);
                    }
                }
                tbodyObj.put(attributeUuid, cellObj);
            }
//            tbodyObj.putAll(extData);
        }
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return null;
    }

    @Override
    public ParamType getParamType() {
        return null;
    }

    @Override
    public boolean isConditionable() {
        return false;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return false;
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return null;
    }

    //表单组件配置信息
//{
//	"handler": "formdynamiclist",
//	"label": "表格选择组件_3",
//	"type": "form",
//	"uuid": "08789b3c99ec4108b6db9184ba7f8743",
//	"config": {
//		"isRequired": false,
//		"relMatrixUuidList": [
//			"8db9a7aa7f8e485d86c97d6ba6d37ae0"
//		],
//		"ruleList": [],
//		"extendAttributes": true,
//		"attributeList": [
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "17ada8f1ace54cf49e95c695988197fd",
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "文本框a",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "文本框a",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "文本框1",
//				"type": "text"
//			}
//		],
//		"pageSize": 10,
//		"validList": [],
//		"matrixType": "custom",
//		"quoteUuid": "",
//		"dataConfig": [
//			{
//				"isSearch": false,
//				"name": "aaa",
//				"isMobile": false,
//				"isSearchable": 1,
//				"isPC": true,
//				"uuid": "e31052d7732345fd825c43a9f9deacb1"
//			},
//			{
//				"isSearch": false,
//				"name": "bbb",
//				"isMobile": false,
//				"isSearchable": 1,
//				"isPC": true,
//				"uuid": "bc0afbec74ea4936acf62ba30e332854"
//			},
//			{
//				"isSearch": false,
//				"name": "ccc",
//				"isMobile": false,
//				"isSearchable": 1,
//				"isPC": true,
//				"uuid": "912e9a241ec24e6cb0f0ad95fb2a7e5b"
//			}
//		],
//		"type": "custom",
//		"matrixUuid": "8db9a7aa7f8e485d86c97d6ba6d37ae0",
//		"mode": "dialog/normal",
//		"needPage": false,
//		"integrationUuid": "5b9d7a56adf54c97a35f54df60f403d9",
//		"uuidColumn": "subSysId",
//		"width": "100%",
//		"defaultValueType": "self",
//		"authorityConfig": [
//			"common#alluser"
//		],
//		"dataSource": "matrix/integration"
//	}
//}
    //保存数据结构
//{
//	"selectUuidList": [
//		"66f1f6137c4e45a18df229f6f029ee59"
//	],
//	"extendedData": {
//		"66f1f6137c4e45a18df229f6f029ee59": {
//			"17ada8f1ace54cf49e95c695988197fd": "文本框a"
//		}
//	},
//	"matrixType": "custom",
//	"table": {
//		"selectUuidList": [],
//		"pageCount": 1,
//		"searchColumnDetailList": [],
//		"rowNum": 1,
//		"pageSize": 100,
//		"currentPage": 1,
//		"type": "custom",
//		"isSelectAll": true,
//		"theadList": [
//			{
//				"title": "aaa",
//				"key": "e31052d7732345fd825c43a9f9deacb1"
//			},
//			{
//				"title": "bbb",
//				"key": "bc0afbec74ea4936acf62ba30e332854"
//			},
//			{
//				"title": "ccc",
//				"key": "912e9a241ec24e6cb0f0ad95fb2a7e5b"
//			},
//			{
//				"isRequired": false,
//				"control": true,
//				"title": "文本框1",
//				"type": "text",
//				"config": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"dataSource": "static",
//					"value": "",
//					"matrixUuid": ""
//				},
//				"key": "bd6ee064bdcd4463ad00848d2cb34c2e"
//			}
//		],
//		"tbodyList": [
//			{
//				"e31052d7732345fd825c43a9f9deacb1": {
//					"text": "111",
//					"type": "input",
//					"value": "111"
//				},
//				"912e9a241ec24e6cb0f0ad95fb2a7e5b": {
//					"vipLevel": 0,
//					"pinyin": "yanya",
//					"text": "闫雅",
//					"type": "user",
//					"value": "002558df23aa7f9addec101c18f94f16"
//				},
//				"uuid": {
//					"text": "66f1f6137c4e45a18df229f6f029ee59",
//					"type": "input",
//					"value": "66f1f6137c4e45a18df229f6f029ee59"
//				},
//				"bc0afbec74ea4936acf62ba30e332854": {
//					"text": "2022-02-22 00:00:00",
//					"type": "date",
//					"value": "2022-02-22 00:00:00"
//				},
//				"bd6ee064bdcd4463ad00848d2cb34c2e": {
//					"isRequired": false,
//					"defaultValue": "",
//					"disabled": false,
//					"text": "",
//					"type": "text",
//					"value": ""
//				},
//				"_isSelected": true
//			}
//		]
//	},
//	"detailData": {
//		"66f1f6137c4e45a18df229f6f029ee59": {
//			"e31052d7732345fd825c43a9f9deacb1": {
//				"text": "111",
//				"type": "input",
//				"value": "111"
//			},
//			"912e9a241ec24e6cb0f0ad95fb2a7e5b": {
//				"text": "闫雅",
//				"type": "user",
//				"value": "002558df23aa7f9addec101c18f94f16"
//			},
//			"17ada8f1ace54cf49e95c695988197fd": {
//				"text": "文本框a",
//				"type": "text",
//				"value": "文本框a"
//			},
//			"uuid": {
//				"text": "66f1f6137c4e45a18df229f6f029ee59",
//				"type": "input",
//				"value": "66f1f6137c4e45a18df229f6f029ee59"
//			},
//			"bc0afbec74ea4936acf62ba30e332854": {
//				"text": "2022-02-22 00:00:00",
//				"type": "date",
//				"value": "2022-02-22 00:00:00"
//			}
//		}
//	}
//}
    //返回数据结构
//{
//  "value": 原始数据
//	"selectUuidList": [
//		"66f1f6137c4e45a18df229f6f029ee59"
//	],
//	"theadList": [
//		{
//			"title": "aaa",
//			"key": "e31052d7732345fd825c43a9f9deacb1"
//		},
//		{
//			"title": "bbb",
//			"key": "bc0afbec74ea4936acf62ba30e332854"
//		},
//		{
//			"title": "ccc",
//			"key": "912e9a241ec24e6cb0f0ad95fb2a7e5b"
//		},
//		{
//			"title": "文本框1",
//			"key": "17ada8f1ace54cf49e95c695988197fd"
//		}
//	],
//	"tbodyList": [
//		{
//			"e31052d7732345fd825c43a9f9deacb1": {
//				"text": "111",
//				"type": "input",
//				"value": "111"
//			},
//			"912e9a241ec24e6cb0f0ad95fb2a7e5b": {
//				"text": "闫雅",
//				"type": "user",
//				"value": "002558df23aa7f9addec101c18f94f16"
//			},
//			"17ada8f1ace54cf49e95c695988197fd": {
//				"text": "文本框a",
//				"type": "text",
//				"value": "文本框a"
//			},
//			"uuid": {
//				"text": "66f1f6137c4e45a18df229f6f029ee59",
//				"type": "input",
//				"value": "66f1f6137c4e45a18df229f6f029ee59"
//			},
//			"bc0afbec74ea4936acf62ba30e332854": {
//				"text": "2022-02-22 00:00:00",
//				"type": "date",
//				"value": "2022-02-22 00:00:00"
//			},
//			"_isSelected": true
//		}
//	]
//}
    /*
    {
        "handler": "formtableselector",
        "reaction": {
            "filter": {},
            "hide": {},
            "readonly": {},
            "disable": {},
            "display": {},
            "emit": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-formdynamiclist",
        "hasValue": true,
        "label": "表格选择_3",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "disableDefaultValue": true,
            "mapping": {},
            "description": "",
            "pageSize": 10,
            "matrixType": "custom",
            "dataConfig": [
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "文本框1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "a22002af151b4da589bc390f3ad164f5"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "下拉框1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "579c9de2986d48738cc0ecafab2f43d3"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "日期1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "b67e647f6fd049e59cc903fb678c7291"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "用户1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "e00fc42fca5d4f5e831d296ed68b3ff1"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "分组1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "14a67eabc2584d4da19c561c26ed0f3a"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "角色1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "93303dc9e96348e68ae5009702958606"
                },
                {
                    "handler": "formtext",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "扩展属性_1",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "147321ac250d48ed9cff09516f47bd8f",
                    "config": {
                        "isRequired": false,
                        "isMask": false,
                        "isHide": false
                    }
                },
                {
                    "handler": "formselect",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "扩展属性_2",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "688dfb6ec05147098cf8e34a1141ad58",
                    "config": {
                        "mapping": {
                            "text": "e00fc42fca5d4f5e831d296ed68b3ff1",
                            "value": "a22002af151b4da589bc390f3ad164f5"
                        },
                        "isMultiple": true,
                        "dataSource": "matrix",
                        "matrixUuid": "e54221ef3b814eebbf57df252426923c"
                    }
                },
                {
                    "handler": "formradio",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "扩展属性_3",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "42dd19dc6a234275bd8d2585d4c2a751",
                    "config": {
                        "dataList": [
                            {
                                "text": "radio1",
                                "value": "1"
                            },
                            {
                                "text": "radio2",
                                "value": "2"
                            }
                        ],
                        "isMultiple": false,
                        "dataSource": "static"
                    }
                }
            ],
            "matrixUuid": "e54221ef3b814eebbf57df252426923c",
            "isHide": false,
            "mode": "dialog",
            "needPage": true,
            "isMask": false,
            "sourceColumnList": [],
            "width": "100%",
            "dataSource": "matrix"
        },
        "uuid": "db70eeb48a144616ab038180e32eea5b"
    }
     */
    /*
    [
        {
            "93303dc9e96348e68ae5009702958606": "R_职位_科技运营中心经理",
            "_selected": false,
            "e00fc42fca5d4f5e831d296ed68b3ff1": "0093a20ff182675fee7b6f821b874488",
            "14a67eabc2584d4da19c561c26ed0f3a": "金融市场总部",
            "a22002af151b4da589bc390f3ad164f5": "2",
            "579c9de2986d48738cc0ecafab2f43d3": "a2",
            "42dd19dc6a234275bd8d2585d4c2a751": "1",
            "b67e647f6fd049e59cc903fb678c7291": "2022-09-24 00:00:00",
            "147321ac250d48ed9cff09516f47bd8f": "扩展属性_1_3",
            "688dfb6ec05147098cf8e34a1141ad58": [
                "6&=&许*航"
            ],
            "uuid": "1b0661a40da3478c9be344c99db2ffb0"
        },
        {
            "93303dc9e96348e68ae5009702958606": "测试用户功能",
            "_selected": false,
            "e00fc42fca5d4f5e831d296ed68b3ff1": "薛*源",
            "14a67eabc2584d4da19c561c26ed0f3a": "纪律监督室",
            "a22002af151b4da589bc390f3ad164f5": "3333",
            "579c9de2986d48738cc0ecafab2f43d3": "a2",
            "42dd19dc6a234275bd8d2585d4c2a751": "2",
            "b67e647f6fd049e59cc903fb678c7291": "2022-12-13 00:00:00",
            "147321ac250d48ed9cff09516f47bd8f": "扩展属性_1_2",
            "688dfb6ec05147098cf8e34a1141ad58": [
                "3333&=&薛*源"
            ],
            "uuid": "8eed18cdbcb34691b379d1b5fc1e3b0f"
        },
        {
            "93303dc9e96348e68ae5009702958606": "测试用户功能",
            "_selected": false,
            "e00fc42fca5d4f5e831d296ed68b3ff1": "冉ss",
            "14a67eabc2584d4da19c561c26ed0f3a": "基金公司（筹建）",
            "a22002af151b4da589bc390f3ad164f5": "1",
            "579c9de2986d48738cc0ecafab2f43d3": "a1",
            "42dd19dc6a234275bd8d2585d4c2a751": "1",
            "b67e647f6fd049e59cc903fb678c7291": "2023-02-10 00:00:00",
            "147321ac250d48ed9cff09516f47bd8f": "扩展属性_1_1",
            "688dfb6ec05147098cf8e34a1141ad58": [
                "3&=&冉ss",
                "6&=&许*航"
            ],
            "uuid": "c8bc60ca9ec740d7a8808753ab504bab"
        }
    ]
     */
    /*

     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        JSONArray dataArray = (JSONArray) attributeDataVo.getDataObj();
        resultObj.put("value", dataArray);
        if (CollectionUtils.isEmpty(dataArray)) {
            return resultObj;
        }
        JSONArray dataConfig = configObj.getJSONArray("dataConfig");
        if (CollectionUtils.isEmpty(dataConfig)) {
            return resultObj;
        }
        JSONObject tableObj = convertTableData(dataArray, dataConfig);
        resultObj.putAll(tableObj);
        return resultObj;
//        JSONObject tableObj = new JSONObject();
//        JSONObject dataObj = (JSONObject) attributeDataVo.getDataObj();
//        tableObj.put("value", dataObj);
//        if (MapUtils.isEmpty(dataObj)) {
//            return tableObj;
//        }
//        JSONArray selectUuidList = dataObj.getJSONArray("selectUuidList");
//        if (CollectionUtils.isEmpty(selectUuidList)) {
//            return tableObj;
//        }
//        tableObj.put("selectUuidList", selectUuidList);
//        String mode = configObj.getString("mode");
//        Boolean needPage = configObj.getBoolean("needPage");
//        if ("normal".equals(mode) && Objects.equals(needPage, false)) {//不分页
//            tableObj.putAll(noNeedPage(dataObj, true));
//        } else {//分页
//            tableObj.putAll(needPage(dataObj, selectUuidList, configObj));
//        }
//        return tableObj;
    }

    /**
     * 将原始数据转换成表格数据（包含theadList、tbodyList）
     * @param dataArray 原始数据
     * @param dataConfig 配置信息
     * @return
     */
    private JSONObject convertTableData(JSONArray dataArray, JSONArray dataConfig) {
        JSONArray theadList = new JSONArray();
        Map<String, String> extraAttributeHandlerMap = new HashMap<>();
        Map<String, JSONObject> extraAttributeConfigMap = new HashMap<>();
        for (int i = 0; i < dataConfig.size(); i++) {
            JSONObject attributeObj = dataConfig.getJSONObject(i);
            String uuid = attributeObj.getString("uuid");
            String label = attributeObj.getString("label");
            String handler = attributeObj.getString("handler");
            Boolean isExtra = attributeObj.getBoolean("isExtra");
            if (Objects.equals(isExtra, true)) {
                JSONObject config = attributeObj.getJSONObject("config");
                extraAttributeConfigMap.put(uuid, config);
                extraAttributeHandlerMap.put(uuid, handler);
            }
            JSONObject theadObj = new JSONObject();
            theadObj.put("title", label);
            theadObj.put("handler", handler);
            theadObj.put("key", uuid);
            theadList.add(theadObj);
        }
        // 不需要解析的属性类型，有文本框、文本域、时间、日期
        List<String> noNeedParseHandlerList = new ArrayList<>();
        noNeedParseHandlerList.add(FormHandler.FORMTEXT.getHandler());
        noNeedParseHandlerList.add(FormHandler.FORMTEXTAREA.getHandler());
        noNeedParseHandlerList.add(FormHandler.FORMTIME.getHandler());
        noNeedParseHandlerList.add(FormHandler.FORMDATE.getHandler());

        // 需要解析的属性类型，有下拉框、单选框、复选框
        List<String> needParseHandlerList = new ArrayList<>();
        needParseHandlerList.add(FormHandler.FORMSELECT.getHandler());
        needParseHandlerList.add(FormHandler.FORMRADIO.getHandler());
        needParseHandlerList.add(FormHandler.FORMCHECKBOX.getHandler());

        JSONArray tbodyList = new JSONArray();
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject newRowDataObj = new JSONObject();
            JSONObject rowDataObj = dataArray.getJSONObject(i);
            for (Map.Entry<String, Object> cellDataObj : rowDataObj.entrySet()) {
                String key = cellDataObj.getKey();
                String handler = extraAttributeHandlerMap.get(key);
                if (StringUtils.isBlank(handler)) {
                    // 非扩展属性
                    newRowDataObj.put(key, cellDataObj.getValue());
                } else if (noNeedParseHandlerList.contains(handler)) {
                    newRowDataObj.put(key, cellDataObj.getValue());
                } else if (needParseHandlerList.contains(handler)) {
                    IFormAttributeDataConversionHandler formAttributeDataConversionHandler = FormAttributeDataConversionHandlerFactory.getHandler(handler);
                    if (formAttributeDataConversionHandler != null) {
                        JSONObject config = extraAttributeConfigMap.get(key);
                        AttributeDataVo attributeDataVo = new AttributeDataVo();
                        attributeDataVo.setDataObj(cellDataObj.getValue());
                        Object result = formAttributeDataConversionHandler.dataTransformationForEmail(attributeDataVo, config);
                        newRowDataObj.put(key, result);
                    } else {
                        newRowDataObj.put(key, "");
                    }
                } else {
                    // 未来增加属性类型，不做解析
                    newRowDataObj.put(key, cellDataObj.getValue());
                }
            }
            tbodyList.add(newRowDataObj);
        }
        return TableResultUtil.getResult(theadList, tbodyList);
    }

    /**
     * 表格选择组件设置分页显示的情况，从原始数据的theadList中获取theadList数据，从原始数据的detailData中获取tbodyList数据
     *
     * @param dataObj        原始数据
     * @param selectUuidList 选择行集合
     * @param configObj      表单组件的配置信息
     * @return
     */
    private JSONObject needPage(JSONObject dataObj, JSONArray selectUuidList, JSONObject configObj) {
        JSONObject tableObj = new JSONObject();
        List<String> columnList = new ArrayList<>();
        JSONArray theadList = new JSONArray();
        JSONArray columnHeadList = configObj.getJSONArray("dataConfig");
        if (CollectionUtils.isNotEmpty(columnHeadList)) {
            for (int i = 0; i < columnHeadList.size(); i++) {
                JSONObject columnHeadObj = columnHeadList.getJSONObject(i);
                Boolean isPC = columnHeadObj.getBoolean("isPC");
                if (Objects.equals(isPC, false)) {
                    continue;
                }
                String uuid = columnHeadObj.getString("uuid");
                if (StringUtils.isBlank(uuid)) {
                    continue;
                }
                String name = columnHeadObj.getString("name");
                JSONObject theadObj = new JSONObject();
                theadObj.put("title", name);
                theadObj.put("key", uuid);
                theadList.add(theadObj);
                columnList.add(uuid);
            }
        }
        JSONArray attributeList = configObj.getJSONArray("attributeList");
        if (CollectionUtils.isNotEmpty(attributeList)) {
            for (int i = 0; i < attributeList.size(); i++) {
                JSONObject attributeObj = attributeList.getJSONObject(i);
                String attributeUuid = attributeObj.getString("attributeUuid");
                if (StringUtils.isBlank(attributeUuid)) {
                    continue;
                }
                if (columnList.contains(attributeUuid)) {
                    continue;
                }
                String attribute = attributeObj.getString("attribute");
                JSONObject theadObj = new JSONObject();
                theadObj.put("title", attribute);
                theadObj.put("key", attributeUuid);
                theadList.add(theadObj);
                columnList.add(attributeUuid);
            }
        }
        JSONObject detailData = dataObj.getJSONObject("detailData");
        if (MapUtils.isEmpty(detailData)) {
            return tableObj;
        }
        JSONArray tbodyList = new JSONArray();
        for (int i = 0; i < selectUuidList.size(); i++) {
            String selectUuid = selectUuidList.getString(i);
            JSONObject rowData = detailData.getJSONObject(selectUuid);
            if (MapUtils.isEmpty(rowData)) {
                continue;
            }
            JSONObject tbodyObj = new JSONObject();
            for (String column : columnList) {
                JSONObject cellObj = rowData.getJSONObject(column);
                if (MapUtils.isEmpty(cellObj)) {
                    continue;
                }
                tbodyObj.put(column, cellObj);
            }
            tbodyObj.put("_isSelected", true);
            tbodyList.add(tbodyObj);
        }
        tableObj.put("theadList", theadList);
        tableObj.put("tbodyList", tbodyList);
        return tableObj;
    }

    /**
     * 表格选择组件设置不分页显示的情况，从表单组件的配置信息中获取theadList数据，从原始数据的tbodyList中获取tbodyList数据
     *
     * @param dataObj             原始数据
     * @param retainNoSelectedRow 是否保留未选中的数据
     * @return
     */
    private JSONObject noNeedPage(JSONObject dataObj, boolean retainNoSelectedRow) {
        JSONObject tableObj = new JSONObject();
        JSONObject table = dataObj.getJSONObject("table");
        if (MapUtils.isNotEmpty(table)) {
            JSONArray theadList = new JSONArray();
            JSONArray theadArray = table.getJSONArray("theadList");
            for (int i = 0; i < theadArray.size(); i++) {
                JSONObject thead = theadArray.getJSONObject(i);
                String key = thead.getString("key");
                String title = thead.getString("title");
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(title)) {
                    JSONObject theadObj = new JSONObject();
                    theadObj.put("key", key);
                    theadObj.put("title", title);
                    theadList.add(theadObj);
                }
            }
            tableObj.put("theadList", theadList);
            JSONArray tbodyList = new JSONArray();
            JSONArray tbodyArray = table.getJSONArray("tbodyList");
            for (int i = 0; i < tbodyArray.size(); i++) {
                JSONObject tbodyObj = new JSONObject();
                JSONObject tbody = tbodyArray.getJSONObject(i);
                for (Map.Entry<String, Object> entry : tbody.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if ("_isSelected".equals(key)) {
                        tbodyObj.put(key, value);
                    } else {
                        JSONObject valueObj = new JSONObject();
                        if (value instanceof JSONObject) {
                            JSONObject valueJSONObject = (JSONObject) value;
                            valueObj.put("value", valueJSONObject.get("value"));
                            valueObj.put("type", valueJSONObject.get("type"));
                            valueObj.put("text", valueJSONObject.get("text"));
                        } else {
                            valueObj.put("value", value);
                            valueObj.put("type", "input");
                            valueObj.put("text", value);
                        }
                        tbodyObj.put(key, valueObj);
                    }
                }
                if (Objects.equals(tbodyObj.getBoolean("_isSelected"), false) && !retainNoSelectedRow) {
                    continue;
                }
                tbodyList.add(tbodyObj);
            }
            tableObj.put("tbodyList", tbodyList);
        }
        return tableObj;
    }

    @Override
    public void makeupFormAttribute(FormAttributeVo formAttributeVo) {
        Set<String> integrationUuidSet = new HashSet<>();
        Set<String> matrixUuidSet = new HashSet<>();
        Map<String, Set<String>> matrixUuidAttributeUuidSetMap = new HashMap<>();
        JSONObject config = formAttributeVo.getConfigObj();
        String dataSource = config.getString("dataSource");
        if ("matrix".equals(dataSource)) {
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isNotBlank(matrixUuid)) {
                Set<String> attributeUuidSet = new HashSet<>();
                matrixUuidSet.add(matrixUuid);
                /** 矩阵属性 **/
                JSONArray dataConfig = config.getJSONArray("dataConfig");
                if (CollectionUtils.isNotEmpty(dataConfig)) {
                    for (int i = 0; i < dataConfig.size(); i++) {
                        JSONObject attributeObj = dataConfig.getJSONObject(i);
                        if (MapUtils.isNotEmpty(attributeObj)) {
                            String uuid = attributeObj.getString("uuid");
                            if (StringUtils.isNotBlank(uuid)) {
                                attributeUuidSet.add(uuid);
                            }
                        }
                    }
                }
                /** 过滤条件 **/
                JSONArray sourceColumnList = config.getJSONArray("sourceColumnList");
                if (CollectionUtils.isNotEmpty(sourceColumnList)) {
                    for (int i = 0; i < sourceColumnList.size(); i++) {
                        JSONObject sourceColumnObj = sourceColumnList.getJSONObject(i);
                        if (MapUtils.isNotEmpty(sourceColumnObj)) {
                            String column = sourceColumnObj.getString("column");
                            if (StringUtils.isNotBlank(column)) {
                                attributeUuidSet.add(column);
                            }
                        }
                    }
                }
                matrixUuidAttributeUuidSetMap.put(matrixUuid, attributeUuidSet);
                /** 扩展属性 **/
                JSONArray attributeArray = config.getJSONArray("attributeList");
                if (CollectionUtils.isNotEmpty(attributeArray)) {
                    for (int i = 0; i < attributeArray.size(); i++) {
                        JSONObject attributeObj = attributeArray.getJSONObject(i);
                        if (MapUtils.isNotEmpty(attributeObj)) {
                            JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                            if (MapUtils.isNotEmpty(attrConfig)) {
                                dataSource = attrConfig.getString("dataSource");
                                if ("matrix".equals(dataSource)) {
                                    matrixUuid = attrConfig.getString("matrixUuid");
                                    if (StringUtils.isNotBlank(matrixUuid)) {
                                        attributeUuidSet = new HashSet<>();
                                        matrixUuidSet.add(matrixUuid);
                                        JSONObject mapping = attrConfig.getJSONObject("mapping");
                                        if (MapUtils.isNotEmpty(mapping)) {
                                            String value = mapping.getString("value");
                                            if (StringUtils.isNotBlank(value)) {
                                                attributeUuidSet.add(value);
                                            }
                                            String text = mapping.getString("text");
                                            if (StringUtils.isNotBlank(text)) {
                                                attributeUuidSet.add(text);
                                            }
                                        }
                                        Set<String> oldAttributeUuidSet = matrixUuidAttributeUuidSetMap.get(matrixUuid);
                                        if (oldAttributeUuidSet != null) {
                                            oldAttributeUuidSet.addAll(attributeUuidSet);
                                        } else {
                                            matrixUuidAttributeUuidSetMap.put(matrixUuid, attributeUuidSet);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if ("integration".equals(dataSource)) {
            String integrationUuid = config.getString("integrationUuid");
            if (StringUtils.isNotBlank(integrationUuid)) {
                integrationUuidSet.add(integrationUuid);
            }
        }

        formAttributeVo.setMatrixUuidSet(matrixUuidSet);
        formAttributeVo.setMatrixUuidAttributeUuidSetMap(matrixUuidAttributeUuidSetMap);
        formAttributeVo.setIntegrationUuidSet(integrationUuidSet);
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject tableObj = new JSONObject();
        JSONObject dataObj = (JSONObject) attributeDataVo.getDataObj();
        tableObj.put("value", dataObj);
        if (MapUtils.isEmpty(dataObj)) {
            return tableObj;
        }
        JSONArray selectUuidList = dataObj.getJSONArray("selectUuidList");
        if (CollectionUtils.isEmpty(selectUuidList)) {
            return tableObj;
        }
        tableObj.put("selectUuidList", selectUuidList);
        String mode = configObj.getString("mode");
        Boolean needPage = configObj.getBoolean("needPage");
        if ("normal".equals(mode) && Objects.equals(needPage, false)) {//不分页
            tableObj.putAll(noNeedPage(dataObj, false)); // 不保留未选中的数据
        } else {//分页
            tableObj.putAll(needPage(dataObj, selectUuidList, configObj));
        }
        return tableObj;
    }

    @Override
    public int getExcelHeadLength(JSONObject configObj) {
        int count = 0;
        JSONArray columnHeadList = configObj.getJSONArray("dataConfig");
        JSONArray attributeList = configObj.getJSONArray("attributeList"); // 扩展属性
        if (CollectionUtils.isNotEmpty(columnHeadList)) {
            for (int i = 0; i < columnHeadList.size(); i++) {
                JSONObject columnHeadObj = columnHeadList.getJSONObject(i);
                Boolean isPC = columnHeadObj.getBoolean("isPC");
                if (Objects.equals(isPC, false)) {
                    continue;
                }
                String uuid = columnHeadObj.getString("uuid");
                if (StringUtils.isBlank(uuid)) {
                    continue;
                }
                count++;
            }
        }
        if (CollectionUtils.isNotEmpty(attributeList)) {
            count += attributeList.size();
        }
        if (count == 0) {
            count++;
        }
        return count;
    }

    @Override
    public int getExcelRowCount(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject dataObj = (JSONObject) attributeDataVo.getDataObj();
        if (MapUtils.isNotEmpty(dataObj)) {
            JSONArray selectUuidList = dataObj.getJSONArray("selectUuidList");
            if (CollectionUtils.isNotEmpty(selectUuidList)) {
                return selectUuidList.size() + 1;
            }
        }
        return 1;
    }
}
