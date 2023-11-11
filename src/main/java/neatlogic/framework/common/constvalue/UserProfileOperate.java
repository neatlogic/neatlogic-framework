package neatlogic.framework.common.constvalue;

import neatlogic.framework.util.$;

public enum UserProfileOperate implements IUserProfileOperate {
    KEEP_ON_CREATE("keeponcreate", "nfcc.userprofileoperate.keeponcreate"),
    EDIT_USER("edituser", "nfcc.userprofileoperate.edituser"),
    BACK_USER_LIST("backuserlist", "nfcc.userprofileoperate.backuserlist"),
    EDIT_ROLE("editrole", "nfcc.userprofileoperate.editrole"),
    BACK_ROLE_LIST("backrolelist", "nfcc.userprofileoperate.backrolelist"),
    EDIT_TEAM("editteam", "nfcc.userprofileoperate.editteam"),
    BACK_TEAM_LIST("backteamlist", "nfcc.userprofileoperate.backteamlist");

    private String value;
    private String text;

    UserProfileOperate(String _value, String _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
    }

}
