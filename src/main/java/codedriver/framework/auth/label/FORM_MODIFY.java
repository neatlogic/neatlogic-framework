/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class FORM_MODIFY extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "表单管理权限";
    }

    @Override
    public String getAuthIntroduction() {
        return "对表单进行添加、修改和删除";
    }

    @Override
    public String getAuthGroup() {
        return "process";
    }

}
