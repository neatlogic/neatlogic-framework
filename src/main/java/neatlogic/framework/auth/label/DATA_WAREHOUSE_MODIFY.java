/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

import java.util.Collections;
import java.util.List;

public class DATA_WAREHOUSE_MODIFY extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "数据仓库管理权限";
    }

    @Override
    public String getAuthIntroduction() {
        return "对数据仓库添加、修改和删除";
    }

    @Override
    public String getAuthGroup() {
        return "framework";
    }

    @Override
    public Integer getSort() {
        return 23;
    }

    @Override
    public List<Class<? extends AuthBase>> getIncludeAuths() {
        return Collections.singletonList(DATA_WAREHOUSE_BASE.class);
    }
}
