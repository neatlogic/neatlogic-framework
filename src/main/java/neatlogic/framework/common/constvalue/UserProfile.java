package neatlogic.framework.common.constvalue;

import neatlogic.framework.util.$;

import java.util.Arrays;
import java.util.List;

public enum UserProfile implements IUserProfile {
    USER_CREATE_SUCESS("usercreatesuccess", "nfcc.userprofile.usercreatesuccess", Arrays.asList(UserProfileOperate.KEEP_ON_CREATE, UserProfileOperate.EDIT_USER, UserProfileOperate.BACK_USER_LIST)),
    ROLE_CREATE_SUCESS("rolecreatesuccess", "nfcc.userprofile.rolecreatesuccess", Arrays.asList(UserProfileOperate.KEEP_ON_CREATE, UserProfileOperate.EDIT_ROLE, UserProfileOperate.BACK_ROLE_LIST)),
    TEAM_CREATE_SUCESS("teamcreatesuccess", "nfcc.userprofile.teamcreatesuccess", Arrays.asList(UserProfileOperate.KEEP_ON_CREATE, UserProfileOperate.EDIT_TEAM, UserProfileOperate.BACK_TEAM_LIST));

    private String value;
    private String text;
    private List<IUserProfileOperate> userProfileOperateList;

    UserProfile(String _value, String _text, List<IUserProfileOperate> _userProfileOperateList) {
        this.value = _value;
        this.text = _text;
        this.userProfileOperateList = _userProfileOperateList;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
    }

    @Override
    public List<IUserProfileOperate> getProfileOperateList() {
        return this.userProfileOperateList;
    }

    @Override
    public String getModuleId() {
        return "framework";
    }

}
