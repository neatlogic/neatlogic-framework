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

package neatlogic.framework.form.dto;

import java.util.*;

import neatlogic.framework.common.dto.BaseEditorVo;
import com.alibaba.fastjson.JSONPath;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

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
                    // tableList?????????????????????
                    JSONArray tableList = this.formConfig.getJSONArray("tableList");
                    if (CollectionUtils.isEmpty(tableList)) {
                        return formAttributeList;
                    }
                    // ????????????????????????????????????????????????????????????????????????????????????
                    Map<String, List<String>> attributeInlineMap = new HashMap<>();
                    // ??????????????????????????????
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
                    // ????????????
                    JSONArray sceneList = this.formConfig.getJSONArray("sceneList");
                    if (StringUtils.isBlank(sceneUuid) || CollectionUtils.isEmpty(sceneList)) {
                        // ????????????UUID??????????????????????????????????????????????????????????????????
                        formAttributeList = defaultSceneFormAttributeList;
                        return formAttributeList;
                    }
                    // ???????????????????????????sceneUuid?????????????????????
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
                    // ???????????????sceneUuid?????????????????????????????????????????????????????????
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
                                formAttributeList.add(new FormAttributeVo(this.getFormUuid(), this.getUuid(), controllerObj.getString("uuid"), controllerObj.getString("label"), controllerObj.getString("type"), controllerObj.getString("handler"), config.getBooleanValue("isRequired"), controllerObj.getString("config"), config.getString("defaultValueList")));
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
            String label = componentObj.getString("label");
            String type = componentObj.getString("type");
            String handler = componentObj.getString("handler");
            boolean isRequired = config.getBooleanValue("isRequired");
            String defaultValue = config.getString("defaultValue");
            return new FormAttributeVo(this.getFormUuid(), this.getUuid(), uuid, label, type, handler, isRequired, componentObj.getString("config"), defaultValue);
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
}
