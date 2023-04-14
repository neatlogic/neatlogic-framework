package neatlogic.framework.common.constvalue;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum UserProfileOperate {
    KEEP_ON_CREATE("keeponcreate", new I18n("enum.framework.userprofileoperate.keep_on_create")),
    EDIT_USER("edituser", new I18n("enum.framework.userprofileoperate.edit_user")),
    BACK_USER_LIST("backuserlist", new I18n("enum.framework.userprofileoperate.back_user_list")),
    EDIT_ROLE("editrole", new I18n("enum.framework.userprofileoperate.edit_role")),
    BACK_ROLE_LIST("backrolelist", new I18n("enum.framework.userprofileoperate.back_role_list")),
    EDIT_TEAM("editteam", new I18n("enum.framework.userprofileoperate.edit_user")),
    BACK_TEAM_LIST("backteamlist", new I18n("enum.framework.userprofileoperate.back_team_list"));

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
        return I18nUtils.getMessage(text.toString());
    }

}
