/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.license;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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
    @EntityField(name = "是否拥有所有权限", type = ApiParamType.BOOLEAN)
    private Boolean isHasAllAuth = false;
    @EntityField(name = "是否超时后拥有所有权限", type = ApiParamType.BOOLEAN)
    private Boolean isExpiredHasAllAuth = false;
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

    public Boolean getHasAllAuth() {
        //如果moduleList name存在 all，则拥有所有权限
        if(isHasAllAuth != null && auth != null && CollectionUtils.isNotEmpty(auth.getModuleList()) && auth.getModuleList().stream().anyMatch(o-> Objects.equals(o.getName().toUpperCase(Locale.ROOT),"ALL"))){
            isHasAllAuth = true;
        }
        return isHasAllAuth;
    }

    public Boolean getExpiredHasAllAuth() {
        //如果moduleList name存在 all，则拥有所有权限
        if(isExpiredHasAllAuth != null && expiredAuth != null && CollectionUtils.isNotEmpty(expiredAuth.getModuleList()) && expiredAuth.getModuleList().stream().anyMatch(o-> Objects.equals(o.getName().toUpperCase(Locale.ROOT),"ALL"))){
            isExpiredHasAllAuth = true;
        }
        return isExpiredHasAllAuth;
    }

    public String getLicenseStr() {
        return licenseStr;
    }

    public void setLicenseStr(String licenseStr) {
        this.licenseStr = licenseStr;
    }
}
