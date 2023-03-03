package neatlogic.framework.dto;

import java.util.Date;

public class UserSessionVo {
	private String userUuid;
	private Date sessionTime;
	
	public UserSessionVo(String userUuid, Date sessionTime) {
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

	public Date getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(Date sessionTime) {
		this.sessionTime = sessionTime;
	}

	
}
