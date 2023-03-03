package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

/**
 * @author longrf
 * @date 2022/4/8 3:49 下午
 */
public class THEME_MODIFY extends AuthBase {

    @Override
    public String getAuthDisplayName() {
        return "auth.framework.thememodify.name";
    }

    @Override
    public String getAuthIntroduction() {
        return "auth.framework.thememodify.introduction";
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
