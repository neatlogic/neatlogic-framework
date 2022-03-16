/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class LicenseVo implements Serializable {
    private static final long serialVersionUID = -4515626151148587123L;
    @EntityField(name = "租户", type = ApiParamType.JSONOBJECT)
    private TenantVo tenantVo;
    @EntityField(name = "到期时间", type = ApiParamType.STRING)
    private Date expireTime;
    @EntityField(name = "权限列表", type = ApiParamType.JSONARRAY)
    private List<String> authList;

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

    public List<String> getAuthList() {
        return authList;
    }

    public void setAuthList(List<String> authList) {
        this.authList = authList;
    }
}
