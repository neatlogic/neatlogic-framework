package codedriver.framework.dto;

public class UserExpirationVo {
	private String userId;
	private String expiredTime;
	
	public UserExpirationVo(String userId, String expiredTime) {
		this.userId = userId;
		this.expiredTime = expiredTime;
	}
	
	public UserExpirationVo() {
		super();
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(String expiredTime) {
		this.expiredTime = expiredTime;
	}
	
	
}
