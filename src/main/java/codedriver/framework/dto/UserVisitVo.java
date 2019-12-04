package codedriver.framework.dto;

public class UserVisitVo {
	private String userId;
	private String visitTime;
	public static final String USER_EXPIRETIME = "USER_EXPIRETIME";
	
	public UserVisitVo(String userId, String visitTime) {
		this.userId = userId;
		this.visitTime = visitTime;
	}
	
	public UserVisitVo() {
		super();
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(String visitTime) {
		this.visitTime = visitTime;
	}

	
}
