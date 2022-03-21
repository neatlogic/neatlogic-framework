/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.license;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.restful.annotation.EntityField;

import java.io.Serializable;
import java.util.Date;

public class LicenseVo implements Serializable {
    private static final long serialVersionUID = -4515626151148587123L;
    @EntityField(name = "租户", type = ApiParamType.JSONOBJECT)
    private TenantVo tenantVo;
    @EntityField(name = "到期时间", type = ApiParamType.STRING)
    private Date expireTime;
    @EntityField(name = "权限列表", type = ApiParamType.JSONOBJECT)
    private LicenseAuthVo licenseAuth;

    public TenantVo getTenantVo() {
        return tenantVo;
    }

    public void setTenantVo(TenantVo tenantVo) {
        this.tenantVo = tenantVo;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public LicenseAuthVo getLicenseAuth() {
        return licenseAuth;
    }

    public void setLicenseAuth(LicenseAuthVo licenseAuth) {
        this.licenseAuth = licenseAuth;
    }
}
