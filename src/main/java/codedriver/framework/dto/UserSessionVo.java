package codedriver.framework.dto;

public class UserSessionVo {
	private String userId;
	private String sessionTime;
	public static final String USER_EXPIRETIME = "USER_EXPIRETIME";
	
	public UserSessionVo(String userId, String sessionTime) {
		this.userId = userId;
		this.sessionTime = sessionTime;
	}
	
	public UserSessionVo() {
		super();
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(String sessionTime) {
		this.sessionTime = sessionTime;
	}

	
}
