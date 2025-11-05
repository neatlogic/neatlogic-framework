package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

public class NoAuth extends AuthBase {
    @Override
    public String getAuthDisplayName() {
        return "无权限";
    }

    @Override
    public String getAuthIntroduction() {
        return "无权限";
    }

    @Override
    public String getAuthGroup() {
        return "framework";
    }

    @Override
    public Integer getSort() {
        return Integer.MAX_VALUE;
    }
}
