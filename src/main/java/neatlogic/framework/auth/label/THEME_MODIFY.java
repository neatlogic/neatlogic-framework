package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

/**
 * @author longrf
 * @date 2022/4/8 3:49 下午
 */
public class THEME_MODIFY extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "主题设置权限";
    }

    @Override
    public String getAuthIntroduction() {
        return "对主题色的设置";
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
