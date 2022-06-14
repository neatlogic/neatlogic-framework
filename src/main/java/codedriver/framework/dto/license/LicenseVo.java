/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.license;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;

public class LicenseVo implements Serializable {
    private static final long serialVersionUID = -4515626151148587123L;
    @EntityField(name = "租户", type = ApiParamType.STRING)
    private String tenant;
    @EntityField(name = "数据库链接url", type = ApiParamType.STRING)
    private String dbUrl;
    @EntityField(name = "到期时间", type = ApiParamType.STRING)
    private Date expireTime;
    @EntityField(name = "权限列表", type = ApiParamType.JSONOBJECT)
    private LicenseAuthVo auth;
    @EntityField(name = "超时权限列表", type = ApiParamType.JSONOBJECT)
    private LicenseAuthVo expiredAuth;
    @EntityField(name = "创建时间", type = ApiParamType.STRING)
    private Date createTime;
    @EntityField(name = "dbUrl是否合法", type = ApiParamType.BOOLEAN)
    private Boolean isDbUrlValid = false;
    @JSONField(serialize = false)
    @EntityField(name = "加密后的license", type = ApiParamType.STRING)
    private String licenseStr;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public LicenseAuthVo getAuth() {
        return auth;
    }

    public void setAuth(LicenseAuthVo auth) {
        this.auth = auth;
    }

    public LicenseAuthVo getExpiredAuth() {
        return expiredAuth;
    }

    public void setExpiredAuth(LicenseAuthVo expiredAuth) {
        this.expiredAuth = expiredAuth;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 超时则获取超时包含all的moduleGroup ，不超时则获取不超时包含all的moduleGroup
     * @return moduleGroup
     */
    public LicenseAuthModuleGroupVo getAllAuthGroup() {
        //如果moduleList name存在 all，则拥有所有权限
        List<LicenseAuthModuleGroupVo> moduleGroupVos = getModuleGroupVoList();
        if(CollectionUtils.isNotEmpty(moduleGroupVos)){
            Optional<LicenseAuthModuleGroupVo> licenseAuthModuleGroupVo = moduleGroupVos.stream().filter(o-> Objects.equals(o.getName().toUpperCase(Locale.ROOT),"ALL")).findFirst();
            if(licenseAuthModuleGroupVo.isPresent()){
                return licenseAuthModuleGroupVo.get();
            }
        }
        return null;
    }
    /**
     * 超时则获取超时moduleGroupList ，不超时则获取不超时的moduleGroupList
     * @return moduleGroupList
     */
    public List<LicenseAuthModuleGroupVo> getModuleGroupVoList() {
        //如果moduleList name存在 all，则拥有所有权限
        List<LicenseAuthModuleGroupVo> moduleGroupVos = auth.getModuleGroupList();
        if (expireTime.getTime() < System.currentTimeMillis()) {
            moduleGroupVos = expiredAuth.getModuleGroupList();
        }
        return moduleGroupVos;
    }

    public String getLicenseStr() {
        return licenseStr;
    }

    public void setLicenseStr(String licenseStr) {
        this.licenseStr = licenseStr;
    }

    public Boolean getIsDbUrlValid(){
        if(isDbUrlValid != null && Config.DB_URL().startsWith(this.dbUrl)){
            isDbUrlValid = true;
        }
        return isDbUrlValid;
    }
}
