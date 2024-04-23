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

package neatlogic.module.framework.form.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dependency.core.DependencyManager;
import neatlogic.framework.form.attribute.core.FormAttributeHandlerFactory;
import neatlogic.framework.form.attribute.core.IFormAttributeHandler;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dao.mapper.FormMapper;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeParentVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.dto.FormVersionVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.form.exception.FormActiveVersionNotFoundExcepiton;
import neatlogic.framework.form.service.IFormCrossoverService;
import neatlogic.framework.matrix.constvalue.SearchExpression;
import neatlogic.framework.matrix.core.IMatrixDataSourceHandler;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerFactory;
import neatlogic.framework.matrix.core.MatrixPrivateDataSourceHandlerFactory;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import neatlogic.framework.matrix.dto.MatrixDefaultValueFilterVo;
import neatlogic.framework.matrix.dto.MatrixKeywordFilterVo;
import neatlogic.framework.matrix.dto.MatrixVo;
import neatlogic.framework.matrix.exception.MatrixDataSourceHandlerNotFoundException;
import neatlogic.framework.matrix.exception.MatrixNotFoundException;
import neatlogic.module.framework.dependency.handler.Matrix2FormAttributeDependencyHandler;
import neatlogic.module.framework.dependency.handler.MatrixAttr2FormAttrDependencyHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FormServiceImpl implements FormService, IFormCrossoverService {
    private static final Logger logger = LoggerFactory.getLogger(FormServiceImpl.class);

    @Resource
    private FormMapper formMapper;

    @Resource
    private MatrixMapper matrixMapper;

    @Override
    public void saveDependency(FormVersionVo formVersion) {
        saveOrDeleteDependency(true, formVersion);
    }

    @Override
    public void deleteDependency(FormVersionVo formVersion) {
        saveOrDeleteDependency(false, formVersion);
    }

    private void saveOrDeleteDependency(boolean isSave, FormVersionVo formVersion) {
        JSONObject formConfig = formVersion.getFormConfig();
        if (MapUtils.isEmpty(formConfig)) {
            return;
        }
        String sceneUuid = formConfig.getString("uuid");
        doSaveOrDeleteDependency(isSave, formVersion.getFormUuid(), formVersion.getUuid(), sceneUuid, formConfig);
    }

    private void doSaveOrDeleteDependency(boolean isSave, String formUuid, String formVersionUuid, String sceneUuid, JSONObject formConfig) {
        if (MapUtils.isEmpty(formConfig)) {
            return;
        }
        JSONArray tableList = formConfig.getJSONArray("tableList");
        if (CollectionUtils.isNotEmpty(tableList)) {
            for (int i = 0; i < tableList.size(); i++) {
                JSONObject tableObj = tableList.getJSONObject(i);
                if (MapUtils.isEmpty(tableObj)) {
                    continue;
                }
                JSONObject component = tableObj.getJSONObject("component");
                doSaveOrDeleteComponentDependency(isSave, formUuid, formVersionUuid, sceneUuid, component);

            }
        }
        JSONArray sceneList = formConfig.getJSONArray("sceneList");
        if (CollectionUtils.isNotEmpty(sceneList)) {
            for (int i = 0; i < sceneList.size(); i++) {
                JSONObject sceneObj = sceneList.getJSONObject(i);
                String sceneUuid2 = sceneObj.getString("uuid");
                doSaveOrDeleteDependency(isSave, formUuid, formVersionUuid, sceneUuid2, sceneObj);
            }
        }
    }

    private void doSaveOrDeleteComponentDependency(boolean isSave, String formUuid, String formVersionUuid, String sceneUuid, JSONObject component) {
        if (MapUtils.isEmpty(component)) {
            return;
        }
        Boolean inherit = component.getBoolean("inherit");
        if (Objects.equals(inherit, true)) {
            return;
        }
        List<String> handlerList = new ArrayList<>();
        handlerList.add(FormHandler.FORMSELECT.getHandler());
        handlerList.add(FormHandler.FORMRADIO.getHandler());
        handlerList.add(FormHandler.FORMCHECKBOX.getHandler());
        handlerList.add(FormHandler.FORMTABLESELECTOR.getHandler());
        String handler = component.getString("handler");
        if (handlerList.contains(handler)) {
            String uuid = component.getString("uuid");
            // 单选框、复选框、下拉框、表格选择组件
            JSONObject config = component.getJSONObject("config");
            if (MapUtils.isEmpty(config)) {
                return;
            }
            String dataSource = config.getString("dataSource");
            if (!Objects.equals(dataSource, "matrix")) {
                return;
            }
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isBlank(matrixUuid)) {
                return;
            }
            if (isSave) {
                JSONObject dependencyConfig = new JSONObject();
                dependencyConfig.put("formUuid", formUuid);
                dependencyConfig.put("formVersionUuid", formVersionUuid);
                dependencyConfig.put("sceneUuid", sceneUuid);
                DependencyManager.insert(Matrix2FormAttributeDependencyHandler.class, matrixUuid, uuid, dependencyConfig);

                dependencyConfig.put("matrixUuid", matrixUuid);
                if (Objects.equals(handler, FormHandler.FORMTABLESELECTOR.getHandler())) {
                    JSONArray dataConfig = config.getJSONArray("dataConfig");
                    if (CollectionUtils.isEmpty(dataConfig)) {
                        return;
                    }
                    for (int i = 0; i < dataConfig.size(); i++) {
                        JSONObject dataObj = dataConfig.getJSONObject(i);
                        if (MapUtils.isEmpty(dataObj)) {
                            continue;
                        }
                        String columnUuid = dataObj.getString("uuid");
                        if (StringUtils.isBlank(columnUuid)) {
                            continue;
                        }
                        DependencyManager.insert(MatrixAttr2FormAttrDependencyHandler.class, columnUuid, uuid, dependencyConfig);
                    }
                } else {
                    JSONObject mapping = config.getJSONObject("mapping");
                    if (MapUtils.isEmpty(mapping)) {
                        return;
                    }
                    String value = mapping.getString("value");
                    String text = mapping.getString("text");
                    if (StringUtils.isNotBlank(value)) {
                        DependencyManager.insert(MatrixAttr2FormAttrDependencyHandler.class, value, uuid, dependencyConfig);
                    }
                    if (StringUtils.isNotBlank(text)) {
                        DependencyManager.insert(MatrixAttr2FormAttrDependencyHandler.class, text, uuid, dependencyConfig);
                    }
                }
            } else {
                DependencyManager.delete(Matrix2FormAttributeDependencyHandler.class, uuid);
                DependencyManager.delete(MatrixAttr2FormAttrDependencyHandler.class, uuid);
            }
        } else if (Objects.equals(handler, FormHandler.FORMTABLEINPUTER.getHandler())) {
            // 表格输入组件
            JSONObject config = component.getJSONObject("config");
            if (MapUtils.isEmpty(config)) {
                return;
            }
            JSONArray dataConfig = config.getJSONArray("dataConfig");
            for (int i = 0; i < dataConfig.size(); i++) {
                JSONObject dataObj = dataConfig.getJSONObject(i);
                if (Objects.equals(dataObj.getString("handler"), "formtable")) {
                    JSONObject config1 = dataObj.getJSONObject("config");
                    if (MapUtils.isEmpty(config1)) {
                        continue;
                    }
                    JSONArray dataConfig1 = config1.getJSONArray("dataConfig");
                    for (int j = 0; j < dataConfig1.size(); j++) {
                        JSONObject dataObj1 = dataConfig1.getJSONObject(j);
                        doSaveOrDeleteComponentDependency(isSave, formUuid, formVersionUuid, sceneUuid, dataObj1);
                    }
                } else {
                    doSaveOrDeleteComponentDependency(isSave, formUuid, formVersionUuid, sceneUuid, dataObj);
                }
            }
        } else if (Objects.equals(handler, FormHandler.FORMTAB.getHandler()) || Objects.equals(handler, FormHandler.FORMCOLLAPSE.getHandler())) {
            // 选项卡、折叠面板
            JSONArray componentArray = component.getJSONArray("component");
            for (int i = 0; i < componentArray.size(); i++) {
                JSONObject componentObj = componentArray.getJSONObject(i);
                doSaveOrDeleteComponentDependency(isSave, formUuid, formVersionUuid, sceneUuid, componentObj);
            }
        } else if (Objects.equals(handler, FormHandler.FORMSUBASSEMBLY.getHandler())) {
            // 子表单
            JSONObject formData = component.getJSONObject("formData");
            if (MapUtils.isEmpty(formData)) {
                return;
            }
            JSONObject formConfig = formData.getJSONObject("formConfig");
            doSaveOrDeleteDependency(isSave, formUuid, formVersionUuid, sceneUuid, formConfig);
        }
    }

    @Override
    public JSONArray staticListPasswordEncrypt(JSONArray data, JSONObject config) {
        if (CollectionUtils.isEmpty(data)) {
            return data;
        }
        List<String> passwordTypeAttributeUuidList = new ArrayList<>();
        List<String> tableTypeAttributeUuidList = new ArrayList<>();
        JSONArray attributeList = config.getJSONArray("attributeList");
        if (CollectionUtils.isEmpty(attributeList)) {
            return data;
        }
        for (int i = 0; i < attributeList.size(); i++) {
            JSONObject attributeObj = attributeList.getJSONObject(i);
            if (MapUtils.isEmpty(attributeObj)) {
                continue;
            }
            String attributeUuid = attributeObj.getString("attributeUuid");
            String type = attributeObj.getString("type");
            if (Objects.equals(type, "password")) {
                passwordTypeAttributeUuidList.add(attributeUuid);
            } else if (Objects.equals(type, "table")) {
                tableTypeAttributeUuidList.add(attributeUuid);
                JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                if (MapUtils.isEmpty(attrConfig)) {
                    continue;
                }
                JSONArray attributeArray = attrConfig.getJSONArray("attributeList");
                if (CollectionUtils.isEmpty(attributeArray)) {
                    continue;
                }
                for (int j = 0; j < attributeArray.size(); j++) {
                    JSONObject attributeObject = attributeArray.getJSONObject(j);
                    if (MapUtils.isEmpty(attributeObject)) {
                        continue;
                    }
                    String attrUuid = attributeObject.getString("attributeUuid");
                    if (Objects.equals(attributeObject.getString("type"), "password")) {
                        passwordTypeAttributeUuidList.add(attrUuid);
                    }
                }
            }
        }
//        JSONObject extendedData = data.getJSONObject("extendedData");
//        if (MapUtils.isNotEmpty(extendedData)) {
//            for (Map.Entry<String, Object> entry : extendedData.entrySet()) {
//               JSONObject rowDataObj = (JSONObject) entry.getValue();
//               for (String key : rowDataObj.keySet()) {
//                   if (passwordTypeAttributeUuidList.contains(key)) {
//                       String value = rowDataObj.getString(key);
//                       if (StringUtils.isNotBlank(value)) {
//                           rowDataObj.put(key, RC4Util.encrypt(value));
//                       }
//                   } else if (tableTypeAttributeUuidList.contains(key)) {
//                       JSONObject tableDataObj = rowDataObj.getJSONObject(key);
//                       for (Map.Entry<String, Object> tableEntry : tableDataObj.entrySet()) {
//                           JSONObject tableRowDataObj = (JSONObject) tableEntry.getValue();
//                           List<String> keyList = new ArrayList<>(tableRowDataObj.keySet());
//                           for (String tableRowKey : keyList) {
//                               if (passwordTypeAttributeUuidList.contains(tableRowKey)) {
//                                   String value = tableRowDataObj.getString(tableRowKey);
//                                   if (StringUtils.isNotBlank(value)) {
//                                       tableRowDataObj.put(tableRowKey, RC4Util.encrypt(value));
//                                   }
//                               }
//                           }
//                       }
//                   }
//               }
//            }
//        }
//        JSONObject detailData = data.getJSONObject("detailData");
//        if (MapUtils.isNotEmpty(detailData)) {
//            for (Map.Entry<String, Object> entry : detailData.entrySet()) {
//                JSONObject rowDataObj = (JSONObject) entry.getValue();
//                for (String key : rowDataObj.keySet()) {
//                    if (passwordTypeAttributeUuidList.contains(key)) {
//                        JSONObject valueObj = rowDataObj.getJSONObject(key);
//                        if (MapUtils.isNotEmpty(valueObj)) {
//                            String value = valueObj.getString("value");
//                            if (StringUtils.isNotBlank(value)) {
//                                valueObj.put("value", RC4Util.encrypt(value));
//                            }
//                            String text = valueObj.getString("text");
//                            if (StringUtils.isNotBlank(text)) {
//                                valueObj.put("text", RC4Util.encrypt(text));
//                            }
//                        }
//                    } else if (tableTypeAttributeUuidList.contains(key)) {
//                        JSONObject tableDataObj = rowDataObj.getJSONObject(key);
//                        tableDataObj = tableDataObj.getJSONObject("value");
//                        for (Map.Entry<String, Object> tableEntry : tableDataObj.entrySet()) {
//                            JSONObject tableRowDataObj = (JSONObject) tableEntry.getValue();
//                            for (String tableRowKey : tableRowDataObj.keySet()) {
//                                if (passwordTypeAttributeUuidList.contains(tableRowKey)) {
//                                    JSONObject valueObj = tableRowDataObj.getJSONObject(tableRowKey);
//                                    if (MapUtils.isNotEmpty(valueObj)) {
//                                        String value = valueObj.getString("value");
//                                        if (StringUtils.isNotBlank(value)) {
//                                            valueObj.put("value", RC4Util.encrypt(value));
//                                        }
//                                        String text = valueObj.getString("text");
//                                        if (StringUtils.isNotBlank(text)) {
//                                            valueObj.put("text", RC4Util.encrypt(text));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
        return data;
    }

    @Override
    public JSONArray textConversionValueForSelectHandler(Object text, JSONObject config) {
        JSONArray valueList = new JSONArray();
        if (text == null) {
            return valueList;
        }
        String dataSource = config.getString("dataSource");
        if ("static".equals(dataSource)) {
            JSONArray dataArray = config.getJSONArray("dataList");
            if (CollectionUtils.isEmpty(dataArray)) {
                return null;
            }
            List<ValueTextVo> dataList = dataArray.toJavaList(ValueTextVo.class);
            Map<String, Object> valueTextMap = dataList.stream().collect(Collectors.toMap(ValueTextVo::getText, ValueTextVo::getValue));
            if (text instanceof String) {
                String textStr = (String) text;
                Object value = valueTextMap.get(textStr);
                if (value != null) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("value", value);
                    jsonObj.put("text", textStr);
                    valueList.add(jsonObj);
                }
                return valueList;
            } else if (text instanceof List) {
                List<String> textList = (List) text;
                if (CollectionUtils.isEmpty(textList)) {
                    return valueList;
                }
                for (String textStr : textList) {
                    Object value = valueTextMap.get(textStr);
                    if (value != null) {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("value", value);
                        jsonObj.put("text", textStr);
                        valueList.add(jsonObj);
                    }
                }
                return valueList;
            }
        } else if ("matrix".equals(dataSource)) {
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isBlank(matrixUuid)) {
                return valueList;
            }
            JSONObject mappingObj = config.getJSONObject("mapping");
            if (MapUtils.isEmpty(mappingObj)) {
                return valueList;
            }
            ValueTextVo mapping = mappingObj.toJavaObject(ValueTextVo.class);
            if (text instanceof String) {
                String textStr = (String) text;
                if (Objects.equals(mapping.getText(), mapping.getValue())) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("value", textStr);
                    jsonObj.put("text", textStr);
                    valueList.add(jsonObj);
                    return valueList;
                }
                String value = getValue(matrixUuid, mapping, textStr);
                if (value != null) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("value", value);
                    jsonObj.put("text", textStr);
                    valueList.add(jsonObj);
                }
                return valueList;
            } else if (text instanceof List) {
                List<String> textList = (List) text;
                if (CollectionUtils.isEmpty(textList)) {
                    return valueList;
                }
                for (String textStr : textList) {
                    if (Objects.equals(mapping.getText(), mapping.getValue())) {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("value", textStr);
                        jsonObj.put("text", textStr);
                        valueList.add(jsonObj);
                    } else {
                        String value = getValue(matrixUuid, mapping, textStr);
                        if (value != null) {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("value", value);
                            jsonObj.put("text", textStr);
                            valueList.add(jsonObj);
                        }
                    }
                }
                return valueList;
            }
        }
        return null;
    }

    @Override
    public void formAttributeValueValid(FormVersionVo formVersionVo, JSONArray formAttributeDataList) throws AttributeValidException {
        if (formVersionVo == null) {
            return;
        }
        List<FormAttributeVo> formAttributeList = formVersionVo.getFormAttributeList();
        if (CollectionUtils.isEmpty(formAttributeList)) {
            return;
        }
        Map<String, FormAttributeVo> formAttributeMap = formAttributeList.stream().collect(Collectors.toMap(FormAttributeVo::getUuid, e -> e));
        for (int i = 0; i < formAttributeDataList.size(); i++) {
            JSONObject formAttributeDataObj = formAttributeDataList.getJSONObject(i);
            String attributeUuid = formAttributeDataObj.getString("attributeUuid");
            FormAttributeVo formAttributeVo = formAttributeMap.get(attributeUuid);
            if (formAttributeVo == null) {
                continue;
            }
            IFormAttributeHandler formAttributeHandler = FormAttributeHandlerFactory.getHandler(formAttributeVo.getHandler());
            if (formAttributeHandler == null) {
                continue;
            }
            Object dataList = formAttributeHandler.conversionDataType(formAttributeDataObj.get("dataList"), formAttributeVo.getLabel());
            formAttributeDataObj.put("dataList", dataList);
//            AttributeDataVo attributeDataVo = new AttributeDataVo();
//            attributeDataVo.setAttributeUuid(formAttributeVo.getUuid());
//            attributeDataVo.setAttributeLabel(formAttributeVo.getLabel());
//            attributeDataVo.setDataObj(formAttributeDataObj.get("dataList"));
//            formAttributeHandler.valid(attributeDataVo, formAttributeVo.getConfigObj());
//            formAttributeDataObj.put("dataList", attributeDataVo.getDataObj());
        }
    }

    @Override
    public JSONObject getMyDetailedDataForSelectHandler(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj == null) {
            return resultObj;
        }
        List<String> valueList = new ArrayList<>();
        List<String> textList = new ArrayList<>();
//        boolean isMultiple = configObj.getBooleanValue("isMultiple");
//        attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
        String dataSource = configObj.getString("dataSource");
        if ("static".equals(dataSource)) {
            JSONArray dataArray = configObj.getJSONArray("dataList");
            if (CollectionUtils.isEmpty(dataArray)) {
                return resultObj;
            }
            List<ValueTextVo> dataList = dataArray.toJavaList(ValueTextVo.class);
            Map<Object, String> valueTextMap = dataList.stream().collect(Collectors.toMap(ValueTextVo::getValue, ValueTextVo::getText));
            if (dataObj instanceof JSONArray) {
                JSONArray valueArray = (JSONArray) dataObj;
                if (CollectionUtils.isNotEmpty(valueArray)) {
//                    valueList = valueArray.toJavaList(String.class);
                    for (Object valueObj : valueArray) {
                        if (valueObj instanceof JSONObject) {
                            JSONObject jsonObj = (JSONObject) valueObj;
                            String value = jsonObj.getString("value");
                            if (value != null) {
                                valueList.add(value);
                            }
                            String text = jsonObj.getString("text");
                            if (text != null) {
                                textList.add(text);
                            } else {
                                if (value != null) {
                                    textList.add(value);
                                }
                            }
                        } else {
                            String text = valueTextMap.get(valueObj);
                            if (text != null) {
                                textList.add(text);
                            } else {
                                textList.add(valueObj.toString());
                            }
                        }
                    }
                }
            } else if (dataObj instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) dataObj;
                String value = jsonObj.getString("value");
                if (value != null) {
                    valueList.add(value);
                }
                String text = jsonObj.getString("text");
                if (text != null) {
                    textList.add(text);
                } else {
                    if (value != null) {
                        textList.add(value);
                    }
                }
            } else {
                String value = dataObj.toString();
                valueList.add(value);
                String text = valueTextMap.get(value);
                if (text != null) {
                    textList.add(text);
                } else {
                    textList.add(value);
                }
            }
        } else {// 其他，如动态数据源
            String matrixUuid = configObj.getString("matrixUuid");
            if (StringUtils.isBlank(matrixUuid)) {
                return resultObj;
            }
            JSONObject mappingObj = configObj.getJSONObject("mapping");
            if (MapUtils.isEmpty(mappingObj)) {
                return resultObj;
            }
            ValueTextVo mapping = mappingObj.toJavaObject(ValueTextVo.class);
            if (dataObj instanceof JSONArray) {
                JSONArray valueArray = (JSONArray) dataObj;
                if (CollectionUtils.isNotEmpty(valueArray)) {
                    for (int i = 0; i < valueArray.size(); i++) {
                        Object obj = valueArray.get(i);
                        if (obj instanceof JSONObject) {
                            JSONObject jsonObj = (JSONObject) obj;
                            String value = jsonObj.getString("value");
                            if (value != null) {
                                valueList.add(value);
                            }
                            String text = jsonObj.getString("text");
                            if (text != null) {
                                textList.add(text);
                            } else {
                                textList.add(value);
                            }
                        } else {
                            String value = obj.toString();
                            valueList.add(value);
                            String text = getText(matrixUuid, mapping, value);
                            if (text != null) {
                                textList.add(text);
                            } else {
                                textList.add(value);
                            }
                        }
                    }
                }
            } else if (dataObj instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) dataObj;
                String value = jsonObj.getString("value");
                if (value != null) {
                    valueList.add(value);
                }
                String text = jsonObj.getString("text");
                if (text != null) {
                    textList.add(text);
                } else {
                    textList.add(value);
                }
            } else {
                String value = dataObj.toString();
                valueList.add(value);
                String text = getText(matrixUuid, mapping, value);
                if (text != null) {
                    textList.add(text);
                } else {
                    textList.add(value);
                }
            }
        }
        resultObj.put("valueList", valueList);
        resultObj.put("textList", textList);
        return resultObj;
    }

    @Override
    public boolean isModifiedFormData(List<FormAttributeVo> formAttributeList,
                                      List<? extends AttributeDataVo> newFormAttributeDataList,
                                      List<? extends AttributeDataVo> oldFormAttributeDataList) {
        boolean isModified = false;
        Map<String, ? extends AttributeDataVo> newFormAttributeDataMap = newFormAttributeDataList.stream().collect(Collectors.toMap(AttributeDataVo::getAttributeUuid, e -> e));
        Map<String, ? extends AttributeDataVo> oldFormAttributeDataMap = oldFormAttributeDataList.stream().collect(Collectors.toMap(AttributeDataVo::getAttributeUuid, e -> e));
        for (FormAttributeVo formAttributeVo : formAttributeList) {
            String attributeUuid = formAttributeVo.getUuid();
            AttributeDataVo newProcessTaskFormAttributeDataVo = newFormAttributeDataMap.get(attributeUuid);
            AttributeDataVo oldProcessTaskFormAttributeDataVo = oldFormAttributeDataMap.get(attributeUuid);
            if (oldProcessTaskFormAttributeDataVo == null && newProcessTaskFormAttributeDataVo == null) {
                continue;
            }
            // 在此之前如果该属性的值，在数据库中没有对应的旧数据
            if (oldProcessTaskFormAttributeDataVo == null) {
                if (newProcessTaskFormAttributeDataVo.getDataObj() != null) {
                    // 现在要保存该属性的值不为null，则将该属性值保存到数据库中，但标记为已修改
                    isModified = true;
                }
            } else if (newProcessTaskFormAttributeDataVo == null) {
                // 如果现在接口参数中没有该属性值，则表示不修改该属性值
            } else if (!Objects.equals(oldProcessTaskFormAttributeDataVo.getDataObj(), newProcessTaskFormAttributeDataVo.getDataObj())) {
                isModified = true;
            }
        }
        return isModified;
    }

    @Override
    public Object getFormSelectAttributeValueByOriginalValue(Object originalValue) {
        return getFormSelectAttributeValueByOriginalValue(originalValue, "value");
    }

    @Override
    public Object getFormSelectAttributeValueByOriginalValue(Object originalValue, String hiddenField) {
        if (originalValue == null) {
            return null;
        }
        if (originalValue instanceof JSONArray) {
            JSONArray valueList = new JSONArray();
            JSONArray originalValueArray = (JSONArray) originalValue;
            for (int i = 0; i < originalValueArray.size(); i++) {
                Object originalValueObject = originalValueArray.get(i);
                if (originalValueObject instanceof JSONObject) {
                    JSONObject originalValueObj = (JSONObject) originalValueObject;
                    Object value = originalValueObj.get(hiddenField);
                    if (value != null) {
                        valueList.add(value);
                    }
                } else {
                    valueList.add(originalValueObject);
                }
            }
            return valueList;
        } else if (originalValue instanceof JSONObject) {
            JSONObject originalValueObj = (JSONObject) originalValue;
            return originalValueObj.get(hiddenField);
        }
        return originalValue;
    }

    private String getValue(String matrixUuid, ValueTextVo mapping, String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        try {
            MatrixVo matrixVo = MatrixPrivateDataSourceHandlerFactory.getMatrixVo(matrixUuid);
            if (matrixVo == null) {
                matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
                if (matrixVo == null) {
                    throw new MatrixNotFoundException(matrixUuid);
                }
            }
            IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrixVo.getType());
            if (matrixDataSourceHandler == null) {
                throw new MatrixDataSourceHandlerNotFoundException(matrixVo.getType());
            }
            String valueField = (String) mapping.getValue();
            String textField = mapping.getText();
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixUuid);
            List<String> columnList = new ArrayList<>();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            dataVo.setColumnList(columnList);
            dataVo.setKeyword(text);
            dataVo.setKeywordColumn(textField);
            for (int i = 0; i < 10; i++) {
                List<Map<String, JSONObject>> tbodyList = matrixDataSourceHandler.searchTableDataNew(dataVo);
                for (Map<String, JSONObject> tbody : tbodyList) {
                    JSONObject textObj = tbody.get(textField);
                    if (Objects.equals(text, textObj.getString("text"))) {
                        JSONObject valueObj = tbody.get(valueField);
                        return valueObj.getString("value");
                    }
                }
                if (dataVo.getCurrentPage() >= dataVo.getPageCount()) {
                    break;
                }
                dataVo.setCurrentPage(dataVo.getCurrentPage() + 1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private String getText(String matrixUuid, ValueTextVo mapping, String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        try {
            MatrixVo matrixVo = MatrixPrivateDataSourceHandlerFactory.getMatrixVo(matrixUuid);
            if (matrixVo == null) {
                matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
                if (matrixVo == null) {
                    throw new MatrixNotFoundException(matrixUuid);
                }
            }
            IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrixVo.getType());
            if (matrixDataSourceHandler == null) {
                throw new MatrixDataSourceHandlerNotFoundException(matrixVo.getType());
            }
            String valueField = (String) mapping.getValue();
            String textField = mapping.getText();
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixUuid);
            List<String> columnList = new ArrayList<>();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            dataVo.setColumnList(columnList);
            List<MatrixDefaultValueFilterVo> defaultValueFilterList = new ArrayList<>();
            MatrixDefaultValueFilterVo matrixDefaultValueFilterVo = new MatrixDefaultValueFilterVo(
                    new MatrixKeywordFilterVo(valueField, SearchExpression.EQ.getExpression(), value),
                    null
            );
            defaultValueFilterList.add(matrixDefaultValueFilterVo);
            dataVo.setDefaultValueFilterList(defaultValueFilterList);
            for (int i = 0; i < 10; i++) {
                List<Map<String, JSONObject>> tbodyList = matrixDataSourceHandler.searchTableDataNew(dataVo);
                for (Map<String, JSONObject> tbody : tbodyList) {
                    JSONObject valueObj = tbody.get(valueField);
                    if (Objects.equals(value, valueObj.getString("value"))) {
                        JSONObject textObj = tbody.get(textField);
                        return textObj.getString("text");
                    }
                }
                if (dataVo.getCurrentPage() >= dataVo.getPageCount()) {
                    break;
                }
                dataVo.setCurrentPage(dataVo.getCurrentPage() + 1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<FormAttributeVo> getAllFormAttributeList(JSONObject formConfig) {
        JSONArray tableList = formConfig.getJSONArray("tableList");
        return getAllFormAttributeList(tableList, null);
    }

    @Override
    public List<FormAttributeVo> getAllFormAttributeList(String formConfig) {
        return getAllFormAttributeList(JSON.parseObject(formConfig));
    }

    @Override
    public FormAttributeVo getFormAttribute(JSONObject formConfig, String attributeUuid, String sceneUuid) {
        JSONArray tableList = null;
        FormAttributeParentVo parent = null;
        String uuid = formConfig.getString("uuid");
        if (sceneUuid == null || Objects.equals(sceneUuid, uuid)) {
            tableList = formConfig.getJSONArray("tableList");
        } else {
            JSONArray sceneList = formConfig.getJSONArray("sceneList");
            for (int i = 0; i < sceneList.size(); i++) {
                JSONObject sceneObj = sceneList.getJSONObject(i);
                uuid = sceneObj.getString("uuid");
                if (Objects.equals(uuid, sceneUuid)) {
                    tableList = sceneObj.getJSONArray("tableList");
                    parent = new FormAttributeParentVo(uuid, sceneObj.getString("name"), null);
                }
            }
        }
        List<FormAttributeVo> formAttributeList = getAllFormAttributeList(tableList, parent);
        for (FormAttributeVo formAttribute : formAttributeList) {
            if (Objects.equals(formAttribute.getUuid(), attributeUuid)) {
                return formAttribute;
            }
        }
        return null;
    }

    @Override
    public FormAttributeVo getFormAttribute(String formConfig, String attributeUuid) {
        return getFormAttribute(JSON.parseObject(formConfig), attributeUuid, null);
    }

    @Override
    public String getFormAttributeHandler(String attributeUuid, JSONObject formConfig) {
        List<FormAttributeVo> formAttributeList = getAllFormAttributeList(formConfig);
        for (FormAttributeVo formAttributeVo : formAttributeList) {
            if (Objects.equals(formAttributeVo.getUuid(), attributeUuid)) {
                return formAttributeVo.getHandler();
            }
        }
        return null;
    }

    @Override
    public String getFormAttributeHandler(String attributeUuid, String formConfig) {
        return getFormAttributeHandler(attributeUuid, JSON.parseObject(formConfig));
    }

    private List<FormAttributeVo> getAllFormAttributeList(JSONArray tableList, FormAttributeParentVo parent) {
        List<FormAttributeVo> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(tableList)) {
            return resultList;
        }
        for (int i = 0; i < tableList.size(); i++) {
            JSONObject cellObj = tableList.getJSONObject(i);
            if (MapUtils.isEmpty(cellObj)) {
                continue;
            }
            JSONObject componentObj = cellObj.getJSONObject("component");
            if (MapUtils.isEmpty(componentObj)) {
                continue;
            }
            resultList.addAll(getFormAttributeList(componentObj, parent));
        }
        return resultList;
    }

    private List<FormAttributeVo> getFormAttributeList(JSONObject componentObj, FormAttributeParentVo parent) {
        List<FormAttributeVo> resultList = new ArrayList<>();
        // 标签组件不能改变值，不放入组件列表里
        String handler = componentObj.getString("handler");
        if (Objects.equals(FormHandler.FORMLABEL.getHandler(), handler)) {
            return resultList;
        }
        FormAttributeVo formAttribute = createFormAttribute(componentObj, parent);
        if (formAttribute != null) {
            resultList.add(formAttribute);
        }
        if (Objects.equals(FormHandler.FORMTABLEINPUTER.getHandler(), handler)) {
            FormAttributeParentVo parent2 = new FormAttributeParentVo(componentObj.getString("uuid"), componentObj.getString("label"), parent);
            JSONObject config = componentObj.getJSONObject("config");
            JSONArray dataConfigList = config.getJSONArray("dataConfig");
            for (int i = 0; i < dataConfigList.size(); i++) {
                JSONObject dataObj = dataConfigList.getJSONObject(i);
                resultList.addAll(getFormAttributeList(dataObj, parent2));
                if (Objects.equals("formtable", dataObj.getString("handler"))) {
                    FormAttributeParentVo parent3 = new FormAttributeParentVo(dataObj.getString("uuid"), dataObj.getString("label"), parent2);
                    JSONObject config2 = dataObj.getJSONObject("config");
                    if (MapUtils.isNotEmpty(config2)) {
                        JSONArray dataConfigList2 = config2.getJSONArray("dataConfig");
                        if (CollectionUtils.isNotEmpty(dataConfigList2)) {
                            for (int j = 0; j < dataConfigList2.size(); j++) {
                                JSONObject dataObj2 = dataConfigList2.getJSONObject(j);
                                resultList.addAll(getFormAttributeList(dataObj2, parent3));
                            }
                        }
                    }
                }
            }
        } else if (Objects.equals(FormHandler.FORMTABLESELECTOR.getHandler(), handler)) {
            FormAttributeParentVo parent2 = new FormAttributeParentVo(componentObj.getString("uuid"), componentObj.getString("label"), parent);
            JSONObject config = componentObj.getJSONObject("config");
            JSONArray dataConfigList = config.getJSONArray("dataConfig");
            for (int i = 0; i < dataConfigList.size(); i++) {
                JSONObject dataObj = dataConfigList.getJSONObject(i);
                resultList.addAll(getFormAttributeList(dataObj, parent2));
            }
        } else if (Objects.equals(FormHandler.FORMSUBASSEMBLY.getHandler(), handler)) {
            FormAttributeParentVo parent2 = new FormAttributeParentVo(componentObj.getString("uuid"), componentObj.getString("label"), parent);
            JSONObject formData = componentObj.getJSONObject("formData");
            if (MapUtils.isNotEmpty(formData)) {
                JSONObject formConfig = formData.getJSONObject("formConfig");
                if (MapUtils.isNotEmpty(formConfig)) {
                    JSONArray tableList2 = formConfig.getJSONArray("tableList");
                    resultList.addAll(getAllFormAttributeList(tableList2, parent2));
                }
            }
        } else {
            JSONArray componentArray = componentObj.getJSONArray("component");
            if (CollectionUtils.isNotEmpty(componentArray)) {
                FormAttributeParentVo parent2 = new FormAttributeParentVo(componentObj.getString("uuid"), componentObj.getString("label"), parent);
                for (int i = 0; i < componentArray.size(); i++) {
                    JSONObject component = componentArray.getJSONObject(i);
                    if (MapUtils.isNotEmpty(component)) {
                        resultList.addAll(getFormAttributeList(component, parent2));
                    }
                }
            }
        }
        return resultList;
    }

    private FormAttributeVo createFormAttribute(JSONObject componentObj, FormAttributeParentVo parent) {
        String uuid = componentObj.getString("uuid");
        if (StringUtils.isBlank(uuid)) {
            return null;
        }
        String handler = componentObj.getString("handler");
        if (StringUtils.isBlank(handler)) {
            return null;
        }
        FormAttributeVo formAttributeVo = new FormAttributeVo();
        formAttributeVo.setUuid(uuid);
        formAttributeVo.setHandler(handler);
        String label = componentObj.getString("label");
        formAttributeVo.setLabel(label);
        String type = componentObj.getString("type");
        formAttributeVo.setType(type);
        JSONObject config = componentObj.getJSONObject("config");
        if (MapUtils.isNotEmpty(config)) {
            boolean isRequired = config.getBooleanValue("isRequired");
            formAttributeVo.setRequired(isRequired);
            String defaultValue = config.getString("defaultValue");
            formAttributeVo.setData(defaultValue);
            formAttributeVo.setConfig(config);
        }
        formAttributeVo.setParent(parent);
        return formAttributeVo;
    }

    @Override
    public List<FormAttributeVo> getFormAttributeList(String formUuid, String formName, String tag) {
        FormVersionVo formVersion = formMapper.getActionFormVersionByFormUuid(formUuid);
        if (formVersion == null) {
            throw new FormActiveVersionNotFoundExcepiton(formName);
        }
        FormAttributeVo searchVo = new FormAttributeVo();
        searchVo.setFormUuid(formUuid);
        searchVo.setFormVersionUuid(formVersion.getUuid());
        List<FormAttributeVo> formAttributeList = formMapper.getFormAttributeList(searchVo);
        if (StringUtils.isBlank(tag)) {
            return formAttributeList;
        }
        List<FormAttributeVo> resultList = new ArrayList<>();
        List<String> parentUuidList = new ArrayList<>();
        List<FormAttributeVo> formExtendAttributeList = new ArrayList<>();
        List<FormAttributeVo> list = formMapper.getFormExtendAttributeListByFormUuidAndFormVersionUuid(formUuid, formVersion.getUuid());
        for (FormAttributeVo formAttributeVo : list) {
            if (Objects.equals(formAttributeVo.getTag(), tag)) {
                formExtendAttributeList.add(formAttributeVo);
                parentUuidList.add(formAttributeVo.getParentUuid());
            }
        }
        for (FormAttributeVo formAttributeVo : formAttributeList) {
            if (parentUuidList.contains(formAttributeVo.getUuid())) {
                continue;
            }
            resultList.add(formAttributeVo);
        }
        resultList.addAll(formExtendAttributeList);
        return resultList;
    }
}
