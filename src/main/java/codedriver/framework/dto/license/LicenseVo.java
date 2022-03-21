/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.license;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.exception.core.LicenseInvalidException;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.RSAUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

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

    public static LicenseVo getInstance(String license) {
        if (StringUtils.isBlank(license)) {
            throw new LicenseInvalidException();
        }
        String[] licenses = license.split("=========================");
        if (licenses.length != 2) {
            throw new LicenseInvalidException();
        }
        String sign = licenses[1];
        byte[] decodeData = Base64.getDecoder().decode(licenses[0]);
        if (!RSAUtils.verify(decodeData, Config.LICENSE_PK, sign)) {
            throw new LicenseInvalidException();
        }
        String licenseData = new String(Objects.requireNonNull(RSAUtils.decryptByPublicKey(decodeData, Config.LICENSE_PK())));
        return JSONObject.parseObject(licenseData).toJavaObject(LicenseVo.class);
    }
}
