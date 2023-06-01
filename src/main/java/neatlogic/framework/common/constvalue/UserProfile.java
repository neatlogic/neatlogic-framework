package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.UserProfileVo;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

import java.util.Arrays;
import java.util.List;

public enum UserProfile implements IUserProfile {
    USER_CREATE_SUCESS("usercreatesuccess", new I18n("用户创建成功"), Arrays.asList(UserProfileOperate.KEEP_ON_CREATE, UserProfileOperate.EDIT_USER, UserProfileOperate.BACK_USER_LIST)),
    ROLE_CREATE_SUCESS("rolecreatesuccess", new I18n("用户组创建成功"), Arrays.asList(UserProfileOperate.KEEP_ON_CREATE, UserProfileOperate.EDIT_ROLE, UserProfileOperate.BACK_ROLE_LIST)),
    TEAM_CREATE_SUCESS("teamcreatesuccess", new I18n("用户组创建成功"), Arrays.asList(UserProfileOperate.KEEP_ON_CREATE, UserProfileOperate.EDIT_TEAM, UserProfileOperate.BACK_TEAM_LIST));

    private String value;
    private I18n text;
    private List<UserProfileOperate> userProfileOperateList;

    private UserProfile(String _value, I18n _text, List<UserProfileOperate> _userProfileOperateList) {
        this.value = _value;
        this.text = _text;
        this.userProfileOperateList = _userProfileOperateList;
    }

    public String getValue() {
        return value;
    }

    public JSONArray getUserProfileOperateList() {
        JSONArray userProfileOperateArray = new JSONArray();
        for (UserProfileOperate userProfileOperate : userProfileOperateList) {
            JSONObject json = new JSONObject();
            json.put("value", userProfileOperate.getValue());
            json.put("text", userProfileOperate.getText());
            userProfileOperateArray.add(json);
        }
        return userProfileOperateArray;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    @Override
    public UserProfileVo getUserProfile() {
        UserProfileVo userProfileVo = new UserProfileVo();
        userProfileVo.setModuleId(getModuleId());
        userProfileVo.setModuleName(ModuleUtil.getModuleById(getModuleId()).getName());
        JSONArray userProfileArray = new JSONArray();
        for (UserProfile f : UserProfile.values()) {
            JSONObject configJson = new JSONObject();
            configJson.put("value", f.getValue());
            configJson.put("text", f.getText());
            configJson.put("userProfileOperateList", f.getUserProfileOperateList());
            userProfileArray.add(configJson);
        }
        userProfileVo.setConfig(userProfileArray.toJSONString());
        return userProfileVo;
    }

    @Override
    public String getModuleId() {
        return "framework";
    }

}
