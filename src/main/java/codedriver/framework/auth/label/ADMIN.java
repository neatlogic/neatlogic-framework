/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class ADMIN extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "管理员权限";
    }

    @Override
    public String getAuthIntroduction() {
        return "对某些系统功能进行管理，例如重建左右编码等";
    }

    @Override
    public String getAuthGroup() {
        return "framework";
    }

    @Override
    public Integer getSort() {
        return 8;
    }

}
