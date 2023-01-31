/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private String formConfig;
    @JSONField(serialize = false)
    private List<FormAttributeVo> formAttributeList;

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

    public String getFormConfig() {
        return formConfig;
    }

    public void setFormConfig(String formConfig) {
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
            if (StringUtils.isNotBlank(this.formConfig)) {
                String _type = (String) JSONPath.read(this.formConfig, "_type");
                if ("new".equals(_type)) {
                    JSONArray tableList = (JSONArray) JSONPath.read(this.formConfig, "tableList");
                    if (CollectionUtils.isEmpty(tableList)) {
                        return formAttributeList;
                    }
                    formAttributeList = new ArrayList<>();
                    for (int i = 0; i < tableList.size(); i++) {
                        JSONObject cellObj = tableList.getJSONObject(i);
                        if (MapUtils.isEmpty(cellObj)) {
                            continue;
                        }
                        JSONObject componentObj = cellObj.getJSONObject("component");
                        if (MapUtils.isEmpty(componentObj)) {
                            continue;
                        }
                        JSONObject config = componentObj.getJSONObject("config");
                        if (MapUtils.isNotEmpty(config)) {
                            String uuid = componentObj.getString("uuid");
                            String label = componentObj.getString("label");
                            String type = componentObj.getString("type");
                            String handler = componentObj.getString("handler");
                            boolean isRequired = config.getBooleanValue("isRequired");
                            String defaultValue = config.getString("defaultValue");
                            formAttributeList.add(new FormAttributeVo(this.getFormUuid(), this.getUuid(), uuid, label, type, handler, isRequired, componentObj.getString("config"), defaultValue));
                        }
                    }
                } else {
                    JSONArray controllerList = (JSONArray) JSONPath.read(this.formConfig, "controllerList");
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

    public void setFormAttributeList(List<FormAttributeVo> formAttributeList) {
        this.formAttributeList = formAttributeList;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

}
