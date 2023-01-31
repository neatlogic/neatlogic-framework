/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

public class GLOBALSEARCH_BASE extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "搜索中心基础权限";
    }

    @Override
    public String getAuthIntroduction() {
        return "拥有此权限才能使用搜索中心基础权限功能";
    }

    @Override
    public String getAuthGroup() {
        return "framework";
    }

    @Override
    public Integer getSort() {
        return 21;
    }
}
