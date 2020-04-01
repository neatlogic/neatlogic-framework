package codedriver.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dto.UserProfileVo;
import codedriver.framework.restful.api.IUserProfile;

public enum UserProfile implements IUserProfile{
	USER_CREATE_SUCESS("usercreatesuccess","用户创建成功"),
	ROLE_CREATE_SUCESS("rolecreatesuccess","角色创建成功"),
	TEAM_CREATE_SUCESS("teamcreatesuccess","用户组创建成功");
	
	private String value;
	private String text;
	
	private UserProfile(String _value,String _text){
		this.value = _value;
		this.text = _text;
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

	@Override
	public UserProfileVo getUserProfile(){
		UserProfileVo userProfileVo = new UserProfileVo();
		userProfileVo.setModuleId(getModuleId());
		userProfileVo.setModuleName(ModuleUtil.getModuleById(getModuleId()).getName());
		JSONArray userProfileArray = new JSONArray();
        for (UserProfile f : UserProfile.values()){
        	JSONObject configJson = new JSONObject();
        	configJson.put("value", f.getValue());
        	configJson.put("text", f.getText());
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
