/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.license;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.exception.core.LicenseInvalidException;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.RSAUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class LicenseVo implements Serializable {
    private static final long serialVersionUID = -4515626151148587123L;
    @EntityField(name = "租户", type = ApiParamType.STRING)
    private String tenant;
    @EntityField(name = "到期时间", type = ApiParamType.STRING)
    private Date expireTime;
    @EntityField(name = "权限列表", type = ApiParamType.JSONOBJECT)
    private LicenseAuthVo auth;
    @EntityField(name = "创建时间", type = ApiParamType.STRING)
    private Date createTime;
    @EntityField(name = "是否拥有所有权限", type = ApiParamType.BOOLEAN)
    private Boolean isHasAllAuth = false;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getHasAllAuth() {
        return isHasAllAuth;
    }

    public void setHasAllAuth(Boolean hasAllAuth) {
        isHasAllAuth = hasAllAuth;
    }

    public static LicenseVo getInstance(String license) {
        if (StringUtils.isBlank(license)) {
            throw new LicenseInvalidException();
        }
        //license = license.replaceAll(System.lineSeparator(),StringUtils.EMPTY);
        String[] licenses = license.split("\n=========================\n");
        if (licenses.length != 2) {
            throw new LicenseInvalidException();
        }
        String sign = licenses[1];
        byte[] decodeData = Base64.getDecoder().decode(licenses[0]);
        if (!RSAUtils.verify(decodeData, Config.LICENSE_PK, sign)) {
            throw new LicenseInvalidException();
        }
        String licenseData = new String(Objects.requireNonNull(RSAUtils.decryptByPublicKey(decodeData, Config.LICENSE_PK())));
        LicenseVo licenseVo = JSONObject.parseObject(licenseData).toJavaObject(LicenseVo.class);
        if(licenseVo.getExpireTime().getTime() < new Date().getTime()){
            throw new LicenseInvalidException();
        }
        //如果moduleGroup 存在 all，则拥有所有权限
        if(licenseVo.getAuth() != null && CollectionUtils.isNotEmpty(licenseVo.getAuth().getModuleGroupList())
          && licenseVo.getAuth().getModuleGroupList().stream().anyMatch(o->Objects.equals(o.toUpperCase(Locale.ROOT),"ALL"))){
            licenseVo.setHasAllAuth(true);
        }
        return licenseVo;
    }
}
