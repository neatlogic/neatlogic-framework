package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

/** 权限名称与说明使用国际化键，权限标识及校验规则保持不变。 */
public class NoAuth extends AuthBase {
    @Override
    public String getAuthDisplayName() {
        return "auth.noauth.name";
    }

    @Override
    public String getAuthIntroduction() {
        return "auth.noauth.description";
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
