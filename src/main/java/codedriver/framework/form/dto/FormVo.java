/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class FormVo extends BasePageVo implements Serializable {

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
    @EntityField(name = "表单内容（表单编辑器使用）", type = ApiParamType.STRING)
    private String formConfig;

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

    public String getFormConfig() {
        return formConfig;
    }

    public void setFormConfig(String formConfig) {
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
