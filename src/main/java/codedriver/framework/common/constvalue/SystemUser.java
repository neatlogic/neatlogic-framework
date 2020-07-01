package codedriver.framework.common.constvalue;

import com.alibaba.fastjson.JSONObject;

public enum SystemUser {
	SYSTEM("system","system","system");
	private String userId;
	private String userUuid;
	private String userName;
	private String timezone = "+8:00";
	private SystemUser(String userId, String userUuid, String userName) {
		this.userId = userId;
		this.userUuid = userUuid;
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public String getUserUuid() {
		return userUuid;
	}
	public String getUserName() {
		return userName;
	}
	
	public String getTimezone() {
		return timezone;
	}
	public JSONObject getConfig() {
		JSONObject config = new JSONObject();
		config.put("userid", userId);
		config.put("useruuid", userUuid);
		config.put("userName", userName);
		return config;
	};
}
