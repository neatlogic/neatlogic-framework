/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.form.attribute.core.IFormAttributeHandler;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.restful.core.MyApiComponent;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentFactory;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;

import java.util.*;

@Component
public class DynamicListHandler extends FormHandlerBase {

    private final static Logger logger = LoggerFactory.getLogger(DynamicListHandler.class);
    @Override
    public String getHandler() {
        return "formdynamiclist";
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
        JSONObject dataObj = (JSONObject) attributeDataVo.getDataObj();
        if (MapUtils.isEmpty(dataObj)) {
            return tableObj;
        }
        JSONArray selectUuidList = dataObj.getJSONArray("selectUuidList");
        if (CollectionUtils.isEmpty(selectUuidList)) {
            return tableObj;
        }
        String mode = configObj.getString("mode");
        Boolean needPage = configObj.getBoolean("needPage");
        String dataSource = configObj.getString("dataSource");
        if ("matrix".equals(dataSource)) {
            if ("normal".equals(mode) && Objects.equals(needPage, false)) {//不分页
                return matrixDataSourceNoNeedPage(dataObj, selectUuidList, configObj);
            } else {//分页
                return matrixDataSourceNeedPage(dataObj, selectUuidList, configObj);
            }
        } else if ("integration".equals(dataSource)) {
            if ("normal".equals(mode) && Objects.equals(needPage, false)) {//不分页
                return integrationDataSourceNoNeedPage(dataObj, selectUuidList, configObj);
            } else {//分页
                return integrationDataSourceNeedPage(dataObj, selectUuidList, configObj);
            }
        }
//        if (Objects.equals(needPage, false)) {//不分页
//            tableObj.put("selectUuidList", selectUuidList);
//            JSONObject table = dataObj.getJSONObject("table");
//            if (MapUtils.isNotEmpty(table)) {
//                JSONArray theadList = table.getJSONArray("theadList");
//                JSONArray tbodyList = table.getJSONArray("tbodyList");
//                JSONArray attributeList = configObj.getJSONArray("attributeList");
//                if (CollectionUtils.isNotEmpty(attributeList)) {
//                    JSONObject valueObj = dataObj.getJSONObject("value");
//                    setExtAttributeData(theadList, tbodyList, configObj, valueObj);
//                }
//                tableObj.put("theadList", theadList);
//                tableObj.put("tbodyList", tbodyList);
//            }
//        } else {//分页
//            List<String> columnList = new ArrayList<>();
//            JSONArray columnHeadList = configObj.getJSONArray("dataConfig");
//            if (CollectionUtils.isNotEmpty(columnHeadList)) {
//                for (int i = 0; i < columnHeadList.size(); i++) {
//                    JSONObject columnHeadObj = columnHeadList.getJSONObject(i);
//                    String uuid = columnHeadObj.getString("uuid");
//                    if (StringUtils.isNotBlank(uuid)) {
//                        columnList.add(uuid);
//                    }
//                }
//            }
//            ApiVo api = PrivateApiComponentFactory.getApiByToken("matrix/column/data/init/fortable");
//            if (api != null) {
//                MyApiComponent restComponent = (MyApiComponent) PrivateApiComponentFactory.getInstance(api.getHandler());
//                if (restComponent != null) {
//                    String matrixUuid = configObj.getString("matrixUuid");
//                    String uuidColumn = configObj.getString("uuidColumn");
//                    List<String> uuidList = selectUuidList.toJavaList(String.class);
//                    JSONObject paramObj = new JSONObject();
//                    paramObj.put("matrixUuid", matrixUuid);
//                    paramObj.put("columnList", columnList);
//                    paramObj.put("uuidList", uuidList);
//                    paramObj.put("uuidColumn", uuidColumn);
//                    paramObj.put("needPage", false);
//                    try {
//                        JSONObject resultObj = (JSONObject) restComponent.myDoService(paramObj);
//                        if (MapUtils.isNotEmpty(resultObj)) {
//                            JSONArray theadList = resultObj.getJSONArray("theadList");
//                            JSONArray tbodyList = resultObj.getJSONArray("tbodyList");
//                            JSONArray attributeList = configObj.getJSONArray("attributeList");
//                            if (CollectionUtils.isNotEmpty(attributeList)) {
//                                JSONObject extendedDataObj = dataObj.getJSONObject("extendedData");
//                                setExtAttributeData(theadList, tbodyList, configObj, extendedDataObj);
//                            }
//                            tableObj.put("selectUuidList", selectUuidList);
//                            tableObj.put("theadList", theadList);
//                            tableObj.put("tbodyList", tbodyList);
//                        }
//                    } catch (Exception e) {
//                        logger.error(e.getMessage(), e);
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
                String uuid = columnHeadObj.getString("uuid");
                if (StringUtils.isNotBlank(uuid)) {
                    columnList.add(uuid);
                }
            }
        }
        ApiVo api = PrivateApiComponentFactory.getApiByToken("matrix/column/data/search/fortable");
        if (api != null) {
            MyApiComponent restComponent = (MyApiComponent) PrivateApiComponentFactory.getInstance(api.getHandler());
            if (restComponent != null) {
                String matrixUuid = configObj.getString("matrixUuid");
                String uuidColumn = configObj.getString("uuidColumn");
                uuidColumn = uuidColumn == null ? "uuid" : uuidColumn;
                JSONObject paramObj = new JSONObject();
                paramObj.put("matrixUuid", matrixUuid);
                paramObj.put("columnList", columnList);
                paramObj.put("defaultValue", selectUuidList);
                paramObj.put("uuidColumn", uuidColumn);
                paramObj.put("needPage", false);
                try {
                    JSONObject resultObj = (JSONObject) restComponent.myDoService(paramObj);
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
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
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
                String uuid = columnHeadObj.getString("uuid");
                if (StringUtils.isNotBlank(uuid)) {
                    columnList.add(uuid);
                }
            }
        }
        ApiVo api = PrivateApiComponentFactory.getApiByToken("integration/table/data/search");
        if (api != null) {
            MyApiComponent restComponent = (MyApiComponent) PrivateApiComponentFactory.getInstance(api.getHandler());
            if (restComponent != null) {
                String integrationUuid = configObj.getString("integrationUuid");
                String uuidColumn = configObj.getString("uuidColumn");
                uuidColumn = uuidColumn == null ? "uuid" : uuidColumn;
                JSONObject paramObj = new JSONObject();
                paramObj.put("integrationUuid", integrationUuid);
                paramObj.put("columnList", columnList);
                paramObj.put("defaultValue", selectUuidList);
                paramObj.put("uuidColumn", uuidColumn);
                paramObj.put("needPage", false);
                try {
                    JSONObject resultObj = (JSONObject) restComponent.myDoService(paramObj);
                    if (MapUtils.isNotEmpty(resultObj)) {
                        Map<String, String> secondEditColumnMap = new HashMap<>();
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
                                    String valueName = config.getString("valueName");
                                    if (StringUtils.isBlank(valueName)) {
                                        valueName = "value";
                                    }
                                    secondEditColumnMap.put(key, valueName);
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
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return tableObj;
    }
    private JSONObject integrationDataSourceNoNeedPage(JSONObject dataObj, JSONArray selectUuidList, JSONObject configObj) {
        JSONObject tableObj = new JSONObject();
        tableObj.put("selectUuidList", selectUuidList);
        JSONObject table = dataObj.getJSONObject("table");
        if (MapUtils.isNotEmpty(table)) {
            String uuidColumn = "uuid";
            Map<String, String> secondEditColumnMap = new HashMap<>();
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
                        String valueName = config.getString("valueName");
                        if (StringUtils.isBlank(valueName)) {
                            valueName = "value";
                        }
                        secondEditColumnMap.put(key, valueName);
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

    /**
     * 处理二次编辑数据
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
                        JSONArray selectedValueArray = extendedRowDataObj.getJSONArray(column);
                        if (CollectionUtils.isNotEmpty(selectedValueArray)) {
                            for (int j = 0; j < columnValueArray.size(); j++) {
                                JSONObject columnValueObj = columnValueArray.getJSONObject(j);
                                Object value = columnValueObj.get(valueName);
                                if (selectedValueArray.contains(value)) {
                                    newColumnValueArray.add(columnValueObj);
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
                            for (String value : valueList.toJavaList(String.class) ) {
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
                            for (String value : valueList.toJavaList(String.class) ) {
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
    public Object textConversionValue(List<String> values, JSONObject config) {
        return null;
    }

    @Override
    public String getHandlerName() {
        return "表格选择组件";
    }

    @Override
    public String getIcon() {
        return "tsfont-formdynamiclist";
    }

    @Override
    public ParamType getParamType() {
        return null;
    }

    @Override
    public String getDataType() {
        return null;
    }

    @Override
    public boolean isConditionable() {
        return false;
    }

    @Override
    public boolean isShowable() {
        return true;
    }

    @Override
    public boolean isValueable() {
        return false;
    }

    @Override
    public boolean isFilterable() {
        return true;
    }

    @Override
    public boolean isExtendable() {
        return false;
    }

    @Override
    public String getModule() {
        return "framework";
    }

    @Override
    public boolean isForTemplate() {
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
}
