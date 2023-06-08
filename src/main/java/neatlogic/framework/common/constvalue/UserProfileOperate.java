package neatlogic.framework.common.constvalue;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum UserProfileOperate {
    KEEP_ON_CREATE("keeponcreate", new I18n("继续创建")),
    EDIT_USER("edituser", new I18n("编辑用户")),
    BACK_USER_LIST("backuserlist", new I18n("返回用户列表")),
    EDIT_ROLE("editrole", new I18n("编辑角色")),
    BACK_ROLE_LIST("backrolelist", new I18n("返回角色列表")),
    EDIT_TEAM("editteam", new I18n("编辑用户")),
    BACK_TEAM_LIST("backteamlist", new I18n("返回用户组列表"));

    private String value;
    private I18n text;

    private UserProfileOperate(String _value, I18n _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }

}
