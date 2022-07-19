/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class MODULE_MODIFY extends AuthBase {
    @Override
    public String getAuthDisplayName() {
        return "系统模块管理权限";
    }

    @Override
    public String getAuthIntroduction() {
        return "查看模块版本信息，导入导出模块初始化数据文件";
    }

    @Override
    public String getAuthGroup() {
        return "framework";
    }

    @Override
    public Integer getSort() {
        return 27;
    }
}
