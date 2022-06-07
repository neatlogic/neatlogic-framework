/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.license;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

import java.util.List;

public class LicenseAuthVo {
    @EntityField(name = "权限列表", type = ApiParamType.JSONARRAY)
    private List<LicenseAuthModuleVo> moduleList;

    public List<LicenseAuthModuleVo> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<LicenseAuthModuleVo> moduleList) {
        this.moduleList = moduleList;
    }
}
