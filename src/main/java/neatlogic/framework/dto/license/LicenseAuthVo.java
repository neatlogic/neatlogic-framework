/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.dto.license;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.List;

public class LicenseAuthVo {
    @EntityField(name = "权限列表", type = ApiParamType.JSONARRAY)
    private List<LicenseAuthModuleGroupVo> moduleGroupList;

    public List<LicenseAuthModuleGroupVo> getModuleGroupList() {
        return moduleGroupList;
    }

    public void setModuleGroupList(List<LicenseAuthModuleGroupVo> moduleGroupList) {
        this.moduleGroupList = moduleGroupList;
    }
}
