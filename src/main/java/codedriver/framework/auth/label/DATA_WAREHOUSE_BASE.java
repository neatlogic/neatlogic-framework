/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class DATA_WAREHOUSE_BASE extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "数据仓库默认权限";
    }

    @Override
    public String getAuthIntroduction() {
        return "对数据仓库数据源进行访问";
    }

    @Override
    public String getAuthGroup() {
        return "framework";
    }

    @Override
    public Integer getSort() {
        return 24;
    }

}
