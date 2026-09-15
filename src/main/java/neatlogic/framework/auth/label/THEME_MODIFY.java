package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

/**
 * @author longrf
 * @date 2022/4/8 3:49 下午
 */
/** 权限名称与说明使用国际化键，权限标识及校验规则保持不变。 */
public class THEME_MODIFY extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "auth.theme_modify.name";
    }

    @Override
    public String getAuthIntroduction() {
        return "auth.theme_modify.description";
    }

    @Override
    public String getAuthGroup() {
        return "framework";
    }

    @Override
    public Integer getSort() {
        return 22;
    }
}
