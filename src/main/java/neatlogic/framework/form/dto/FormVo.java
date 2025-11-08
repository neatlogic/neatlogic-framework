/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.form.dto;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

public class FormVo extends BaseEditorVo {

    private static final long serialVersionUID = -2319081254327257337L;

    @EntityField(name = "表单uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "表单名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "当前版本", type = ApiParamType.INTEGER)
    private Integer currentVersion;
    @EntityField(name = "当前版本uuid", type = ApiParamType.STRING)
    private String currentVersionUuid;
    @EntityField(name = "表单内容（表单编辑器使用）", type = ApiParamType.JSONOBJECT)
    private JSONObject formConfig;
    @EntityField(name = "引用数量", type = ApiParamType.INTEGER)
    private int referenceCount;

    @EntityField(name = "版本信息列表", type = ApiParamType.JSONARRAY)
    private List<FormVersionVo> versionList;

    public synchronized String getUuid() {
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString().replace("-", "");
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public List<FormVersionVo> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<FormVersionVo> versionList) {
        this.versionList = versionList;
    }

    public JSONObject getFormConfig() {
        return formConfig;
    }

    public void setFormConfig(JSONObject formConfig) {
        this.formConfig = formConfig;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getCurrentVersionUuid() {
        return currentVersionUuid;
    }

    public void setCurrentVersionUuid(String currentVersionUuid) {
        this.currentVersionUuid = currentVersionUuid;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }

}
