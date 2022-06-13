/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.license;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

import java.util.List;

public class LicenseAuthModuleGroupVo {
    @EntityField(name = "模块名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "权限列表", type = ApiParamType.JSONARRAY)
    private List<String> authList;
    @EntityField(name = "操作列表", type = ApiParamType.JSONARRAY)
    private List<String> operationTypeList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAuthList() {
        return authList;
    }

    public void setAuthList(List<String> authList) {
        this.authList = authList;
    }

    public List<String> getOperationTypeList() {
        return operationTypeList;
    }

    public void setOperationTypeList(List<String> operationTypeList) {
        this.operationTypeList = operationTypeList;
    }
}
