package codedriver.framework.dto;

public class UserSessionVo {
	private String userUuid;
	private String sessionTime;
	public static final String USER_EXPIRETIME = "USER_EXPIRETIME";
	
	public UserSessionVo(String userUuid, String sessionTime) {
		this.userUuid = userUuid;
		this.sessionTime = sessionTime;
	}
	
	public UserSessionVo() {
		super();
	}

	public String getUserUuid() {
		return userUuid;
	}
	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(String sessionTime) {
		this.sessionTime = sessionTime;
	}

	
}
