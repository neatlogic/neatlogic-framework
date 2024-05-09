/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dependency.core.DependencyManager;
import neatlogic.framework.form.attribute.core.FormAttributeHandlerFactory;
import neatlogic.framework.form.attribute.core.IFormAttributeHandler;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeParentVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.dto.FormVersionVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.module.framework.dependency.handler.Matrix2FormAttributeDependencyHandler;
import neatlogic.module.framework.dependency.handler.MatrixAttr2FormAttrDependencyHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FormUtil {
    /**
     * 保存表单属性与其他功能的引用关系
     * @param formVersion
     */
    public static void saveDependency(FormVersionVo formVersion) {
        saveOrDeleteDependency(true, formVersion);
    }

    /**
     * 删除表单属性与其他功能的引用关系
     * @param formVersion
     */
    public static void deleteDependency(FormVersionVo formVersion) {
        saveOrDeleteDependency(false, formVersion);
    }

    private static void saveOrDeleteDependency(boolean isSave, FormVersionVo formVersion) {
        JSONObject formConfig = formVersion.getFormConfig();
        if (MapUtils.isEmpty(formConfig)) {
            return;
        }
        String sceneUuid = formConfig.getString("uuid");
        doSaveOrDeleteDependency(isSave, formVersion.getFormUuid(), formVersion.getUuid(), sceneUuid, formConfig);
    }

    private static void doSaveOrDeleteDependency(boolean isSave, String formUuid, String formVersionUuid, String sceneUuid, JSONObject formConfig) {
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

    private static void doSaveOrDeleteComponentDependency(boolean isSave, String formUuid, String formVersionUuid, String sceneUuid, JSONObject component) {
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

    /**
     * 校验表单数据有效性，并针对特殊组件数据进行相应处理，如密码类型组件对数据进行加密处理
     * @param formVersionVo
     * @param formAttributeDataList
     * @throws AttributeValidException
     */
    public static void formAttributeValueValid(FormVersionVo formVersionVo, JSONArray formAttributeDataList) throws AttributeValidException {
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
        }
    }

    /**
     * 判断是否修改了表单数据
     * @param formAttributeList 表单属性列表
     * @param newFormAttributeDataList 新的表单属性数据列表
     * @param oldFormAttributeDataList 旧的表单属性数据列表
     * @return
     */
    public static boolean isModifiedFormData(List<FormAttributeVo> formAttributeList,
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

    /**
     * 获取表单下拉框组件value值
     * @param originalValue 原始值，json对象
     * @return
     */
    public static Object getFormSelectAttributeValueByOriginalValue(Object originalValue) {
        return getFormSelectAttributeValueByOriginalValue(originalValue, "value");
    }

    /**
     * 获取表单下拉框组件value或隐藏属性值
     * @param originalValue 原始值，json对象
     * @param hiddenField 隐藏属性key
     * @return
     */
    public static Object getFormSelectAttributeValueByOriginalValue(Object originalValue, String hiddenField) {
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

    /**
     * 根据表单配置信息解析出表单的所有组件列表，包括子表单中的组件
     * @param formConfig
     * @return
     */
    public static List<FormAttributeVo> getAllFormAttributeList(JSONObject formConfig) {
        JSONArray tableList = formConfig.getJSONArray("tableList");
        return getAllFormAttributeList(tableList, null);
    }

    /**
     * 根据表单配置信息解析出表单的所有组件列表，包括子表单中的组件
     * @param formConfig
     * @return
     */
    public static List<FormAttributeVo> getAllFormAttributeList(String formConfig) {
        return getAllFormAttributeList(JSON.parseObject(formConfig));
    }

    /**
     * 根据表单配置信息，表单组件uuid，场景uuid，获取表单组件信息
     * @param formConfig
     * @param attributeUuid
     * @param sceneUuid
     * @return
     */
    public static FormAttributeVo getFormAttribute(JSONObject formConfig, String attributeUuid, String sceneUuid) {
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

    /**
     * 根据表单配置信息，表单组件uuid，场景uuid，获取表单组件信息
     * @param formConfig
     * @param attributeUuid
     * @return
     */
    public static FormAttributeVo getFormAttribute(String formConfig, String attributeUuid) {
        return getFormAttribute(JSON.parseObject(formConfig), attributeUuid, null);
    }

    /**
     * 获取表单组件类型
     * @param attributeUuid 属性唯一标识
     * @param formConfig 表单版本配置信息
     * @return
     */
    public static String getFormAttributeHandler(String attributeUuid, JSONObject formConfig) {
        List<FormAttributeVo> formAttributeList = getAllFormAttributeList(formConfig);
        for (FormAttributeVo formAttributeVo : formAttributeList) {
            if (Objects.equals(formAttributeVo.getUuid(), attributeUuid)) {
                return formAttributeVo.getHandler();
            }
        }
        return null;
    }

    /**
     * 获取表单组件类型
     * @param attributeUuid 属性唯一标识
     * @param formConfig 表单版本配置信息
     * @return
     */
    public static String getFormAttributeHandler(String attributeUuid, String formConfig) {
        return getFormAttributeHandler(attributeUuid, JSON.parseObject(formConfig));
    }

    private static List<FormAttributeVo> getAllFormAttributeList(JSONArray tableList, FormAttributeParentVo parent) {
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

    public static List<FormAttributeVo> getFormAttributeList(JSONObject componentObj, FormAttributeParentVo parent) {
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

    private static FormAttributeVo createFormAttribute(JSONObject componentObj, FormAttributeParentVo parent) {
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

}
