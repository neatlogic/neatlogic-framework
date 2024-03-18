/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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
