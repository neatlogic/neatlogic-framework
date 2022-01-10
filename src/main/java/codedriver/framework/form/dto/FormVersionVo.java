/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import codedriver.framework.common.dto.BaseEditorVo;
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
//    private String editor;
//    private Date editTime;
    @JSONField(serialize = false)
    private List<FormAttributeVo> formAttributeList;
    @JSONField(serialize = false)
    private List<FormAttributeMatrixVo> processMatrixFormComponentList;

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

//    public String getEditor() {
//        if (StringUtils.isBlank(editor)) {
//            editor = UserContext.get().getUserUuid();
//        }
//        return editor;
//    }
//
//    public void setEditor(String editor) {
//        this.editor = editor;
//    }
//
//    public Date getEditTime() {
//        return editTime;
//    }
//
//    public void setEditTime(Date editTime) {
//        this.editTime = editTime;
//    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<FormAttributeVo> getFormAttributeList() {
        if (formAttributeList == null) {
            if (StringUtils.isNotBlank(this.formConfig)) {
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
