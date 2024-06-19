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

package neatlogic.framework.form.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.util.UuidUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FormVersionVo extends BaseEditorVo {
    private static final long serialVersionUID = 8345592242508980127L;
    private String uuid;
    private String formName;
    private String formUuid;
    private Integer version;
    private Integer isActive;
    private JSONObject formConfig;
    private String sceneUuid;
    @JSONField(serialize = false)
    private List<FormAttributeVo> formCustomExtendAttributeList;
    @JSONField(serialize = false)
    private List<FormAttributeVo> formExtendAttributeList;
    @JSONField(serialize = false)
    private List<FormAttributeVo> formAttributeList;
    @JSONField(serialize = false)
    private String formConfigStr;

    public synchronized String getUuid() {
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString().replace("-", "");
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public JSONObject getFormConfig() {
        if (formConfig == null && StringUtils.isNotBlank(formConfigStr)) {
            try {
                formConfig = JSONObject.parseObject(formConfigStr);
            } catch (Exception ignored) {

            }
        }
        return formConfig;
    }

    public void setFormConfig(JSONObject formConfig) {
        this.formConfigStr = null;
        this.formConfig = formConfig;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<FormAttributeVo> getFormAttributeList() {
        if (formAttributeList == null) {
            if (MapUtils.isNotEmpty(getFormConfig())) {
                String _type = this.formConfig.getString("_type");
                if ("new".equals(_type)) {
                    // tableList是默认场景数据
                    JSONArray tableList = this.formConfig.getJSONArray("tableList");
                    if (CollectionUtils.isEmpty(tableList)) {
                        return formAttributeList;
                    }
                    // 内嵌组件关系映射，例如选项卡组件里面可以包含几个其他组件
                    Map<String, List<String>> attributeInlineMap = new HashMap<>();
                    // 默认场景里的组件列表
                    List<FormAttributeVo> defaultSceneFormAttributeList = new ArrayList<>();
                    for (int i = 0; i < tableList.size(); i++) {
                        JSONObject cellObj = tableList.getJSONObject(i);
                        if (MapUtils.isEmpty(cellObj)) {
                            continue;
                        }
                        JSONObject componentObj = cellObj.getJSONObject("component");
                        if (MapUtils.isEmpty(componentObj)) {
                            continue;
                        }
                        // 标签组件不能改变值，不放入组件列表里
                        if (Objects.equals("formlabel", componentObj.getString("handler"))) {
                            continue;
                        }
                        FormAttributeVo formAttributeVo = createFormAttribute(componentObj);
                        if (formAttributeVo != null) {
                            defaultSceneFormAttributeList.add(formAttributeVo);
                        }
                        JSONArray componentArray = componentObj.getJSONArray("component");
                        if (CollectionUtils.isNotEmpty(componentArray)) {
                            String uuid = componentObj.getString("uuid");
                            List<String> uuidList = new ArrayList<>();
                            for (int j = 0; j < componentArray.size(); j++) {
                                JSONObject component = componentArray.getJSONObject(j);
                                if (MapUtils.isNotEmpty(component)) {
                                    formAttributeVo = createFormAttribute(component);
                                    if (formAttributeVo != null) {
                                        defaultSceneFormAttributeList.add(formAttributeVo);
                                        uuidList.add(formAttributeVo.getUuid());
                                    }
                                }
                            }
                            attributeInlineMap.put(uuid, uuidList);
                        }
                    }
                    String defaultSceneUuid = this.formConfig.getString("defaultSceneUuid");
                    if (StringUtils.isBlank(sceneUuid) && StringUtils.isNotBlank(defaultSceneUuid)) {
                        sceneUuid = defaultSceneUuid;
                    }
                    String mainSceneUuid = this.formConfig.getString("uuid");
                    // 场景列表
                    JSONArray sceneList = this.formConfig.getJSONArray("sceneList");
                    if (Objects.equals(mainSceneUuid, sceneUuid) || StringUtils.isBlank(sceneUuid) || CollectionUtils.isEmpty(sceneList)) {
                        // 如果场景UUID为空或者场景列表为空，则返回默认场景组件列表
                        formAttributeList = defaultSceneFormAttributeList;
                        return formAttributeList;
                    }
                    // 遍历场景列表，找到sceneUuid对应的场景信息
                    for (int i = 0; i < sceneList.size(); i++) {
                        JSONObject scene = sceneList.getJSONObject(i);
                        if (MapUtils.isEmpty(scene)) {
                            continue;
                        }
                        if (!Objects.equals(sceneUuid, scene.getString("uuid"))) {
                            continue;
                        }
                        formAttributeList = new ArrayList<>();
                        JSONArray sceneTableList = scene.getJSONArray("tableList");
                        if (CollectionUtils.isEmpty(sceneTableList)) {
                            return formAttributeList;
                        }
                        for (int j = 0; j < sceneTableList.size(); j++) {
                            JSONObject sceneTable = sceneTableList.getJSONObject(j);
                            if (MapUtils.isEmpty(sceneTable)) {
                                continue;
                            }
                            JSONObject componentObj = sceneTable.getJSONObject("component");
                            if (MapUtils.isEmpty(componentObj)) {
                                continue;
                            }
                            Boolean inherit = componentObj.getBoolean("inherit");
                            if (Objects.equals(inherit, true)) {
                                String uuid = componentObj.getString("uuid");
                                if (StringUtils.isBlank(uuid)) {
                                    continue;
                                }
                                List<String> uuidList = attributeInlineMap.computeIfAbsent(uuid, key -> new ArrayList<>());
                                uuidList.add(uuid);
                                for (FormAttributeVo formAttributeVo : defaultSceneFormAttributeList) {
                                    if (uuidList.contains(formAttributeVo.getUuid())) {
                                        formAttributeList.add(formAttributeVo);
                                    }
                                }
                            } else {
                                FormAttributeVo formAttributeVo = createFormAttribute(componentObj);
                                if (formAttributeVo != null) {
                                    formAttributeList.add(formAttributeVo);
                                }
                                JSONArray componentArray = componentObj.getJSONArray("component");
                                if (CollectionUtils.isNotEmpty(componentArray)) {
                                    for (int k = 0; k < componentArray.size(); k++) {
                                        JSONObject component = componentArray.getJSONObject(k);
                                        if (MapUtils.isNotEmpty(component)) {
                                            formAttributeVo = createFormAttribute(component);
                                            if (formAttributeVo != null) {
                                                formAttributeList.add(formAttributeVo);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return formAttributeList;
                    }
                    // 如果找不到sceneUuid对应的场景信息，则返回默认场景组件列表
                    formAttributeList = defaultSceneFormAttributeList;
                    return formAttributeList;
                } else {
                    JSONArray controllerList = this.formConfig.getJSONArray("controllerList");
                    if (CollectionUtils.isNotEmpty(controllerList)) {
                        formAttributeList = new ArrayList<>();
                        for (int i = 0; i < controllerList.size(); i++) {
                            JSONObject controllerObj = controllerList.getJSONObject(i);
                            JSONObject config = controllerObj.getJSONObject("config");
                            if (MapUtils.isNotEmpty(config)) {
                                formAttributeList.add(new FormAttributeVo(this.getFormUuid(), this.getUuid(), controllerObj.getString("uuid"), null, controllerObj.getString("label"), controllerObj.getString("type"), controllerObj.getString("handler"), config.getBooleanValue("isRequired"), config, config.getString("defaultValueList")));
                            }
                        }
                    }
                }
            }
        }
        return formAttributeList;
    }

    private FormAttributeVo createFormAttribute(JSONObject componentObj) {
        JSONObject config = componentObj.getJSONObject("config");
        if (MapUtils.isNotEmpty(config)) {
            String uuid = componentObj.getString("uuid");
            String key = componentObj.getString("key");
            String label = componentObj.getString("label");
            String type = componentObj.getString("type");
            String handler = componentObj.getString("handler");
            boolean isRequired = config.getBooleanValue("isRequired");
            String defaultValue = config.getString("defaultValue");
            return new FormAttributeVo(this.getFormUuid(), this.getUuid(), uuid, key, label, type, handler, isRequired, config, defaultValue);
        }
        return null;
    }

    public void setFormAttributeList(List<FormAttributeVo> formAttributeList) {
        this.formAttributeList = formAttributeList;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getSceneUuid() {
        return sceneUuid;
    }

    public void setSceneUuid(String sceneUuid) {
        this.sceneUuid = sceneUuid;
    }

    public String getFormConfigStr() {
        if (StringUtils.isBlank(formConfigStr) && formConfig != null) {
            formConfigStr = formConfig.toJSONString();
        }
        return formConfigStr;
    }

    public void setFormConfigStr(String formConfigStr) {
        this.formConfigStr = formConfigStr;
    }

    public List<FormAttributeVo> getFormExtendAttributeList() {
        if (formExtendAttributeList == null) {
            if (MapUtils.isNotEmpty(getFormConfig())) {
                JSONObject formExtendConfig = this.formConfig.getJSONObject("formExtendConfig");
                if (MapUtils.isNotEmpty(formExtendConfig)) {
                    JSONArray attributeArray = formExtendConfig.getJSONArray("attributeList");
                    if (CollectionUtils.isNotEmpty(attributeArray)) {
                        formExtendAttributeList = new ArrayList<>();
                        for (int i = 0; i < attributeArray.size(); i++) {
                            JSONObject attributeObj = attributeArray.getJSONObject(i);
                            if (MapUtils.isEmpty(attributeObj)) {
                                continue;
                            }
                            String parentUuid = attributeObj.getString("parentUuid");
                            String tag = attributeObj.getString("tag");
                            String key = attributeObj.getString("key");
                            String label = attributeObj.getString("label");
                            String type = attributeObj.getString("type");
                            String handler = attributeObj.getString("handler");
                            JSONObject config = attributeObj.getJSONObject("config");
                            FormAttributeVo formAttributeVo = new FormAttributeVo();
                            formAttributeVo.setFormUuid(formUuid);
                            formAttributeVo.setFormVersionUuid(uuid);
                            formAttributeVo.setParentUuid(parentUuid);
                            formAttributeVo.setTag(tag);
                            formAttributeVo.setKey(key);
                            formAttributeVo.setUuid(UuidUtil.getCustomUUID(parentUuid + "#" + tag + "#" + key));
                            formAttributeVo.setLabel(label);
                            formAttributeVo.setType(type);
                            formAttributeVo.setHandler(handler);
                            formAttributeVo.setConfig(config);
                            formExtendAttributeList.add(formAttributeVo);
                        }
                    }
                }
            }
        }
        return formExtendAttributeList;
    }

    public void setFormExtendAttributeList(List<FormAttributeVo> formExtendAttributeList) {
        this.formExtendAttributeList = formExtendAttributeList;
    }

    public List<FormAttributeVo> getFormCustomExtendAttributeList() {
        if (formCustomExtendAttributeList == null) {
            if (MapUtils.isNotEmpty(getFormConfig())) {
                JSONObject formCustomExtendConfig = this.formConfig.getJSONObject("formCustomExtendConfig");
                if (MapUtils.isNotEmpty(formCustomExtendConfig)) {
                    JSONArray extendConfigList = formCustomExtendConfig.getJSONArray("extendConfigList");
                    if (CollectionUtils.isNotEmpty(extendConfigList)) {
                        String mainSceneUuid = this.formConfig.getString("uuid");
                        formCustomExtendAttributeList = new ArrayList<>();
                        for (int i = 0; i < extendConfigList.size(); i++) {
                            JSONObject extendConfigObj = extendConfigList.getJSONObject(i);
                            if (MapUtils.isEmpty(extendConfigObj)) {
                                continue;
                            }
                            JSONArray attributeArray = extendConfigObj.getJSONArray("attributeList");
                            if (CollectionUtils.isNotEmpty(attributeArray)) {
                                for (int j = 0; j < attributeArray.size(); j++) {
                                    JSONObject attributeObj = attributeArray.getJSONObject(j);
                                    if (MapUtils.isEmpty(attributeObj)) {
                                        continue;
                                    }
                                    String tag = attributeObj.getString("tag");
                                    String key = attributeObj.getString("key");
                                    String label = attributeObj.getString("label");
                                    String type = attributeObj.getString("type");
                                    String handler = attributeObj.getString("handler");
                                    JSONObject config = attributeObj.getJSONObject("config");
                                    FormAttributeVo formAttributeVo = new FormAttributeVo();
                                    formAttributeVo.setFormUuid(formUuid);
                                    formAttributeVo.setFormVersionUuid(uuid);
                                    formAttributeVo.setTag(tag);
                                    formAttributeVo.setKey(key);
                                    formAttributeVo.setUuid(UuidUtil.getCustomUUID(mainSceneUuid + "#" + tag + "#" + key));
                                    formAttributeVo.setLabel(label);
                                    formAttributeVo.setType(type);
                                    formAttributeVo.setHandler(handler);
                                    formAttributeVo.setConfig(config);
                                    formCustomExtendAttributeList.add(formAttributeVo);
                                }
                            }
                        }
                    }
                }
            }
        }
        return formCustomExtendAttributeList;
    }

    public void setFormCustomExtendAttributeList(List<FormAttributeVo> formCustomExtendAttributeList) {
        this.formCustomExtendAttributeList = formCustomExtendAttributeList;
    }
}
