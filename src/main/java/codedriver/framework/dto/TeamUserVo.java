package codedriver.framework.dto;

public class TeamUserVo {
	
	private String teamUuid;
	private String userUuid;
	private String userName;
	private String teamName;
	private String title = "generalstaff";
	
	public TeamUserVo() {
	}

	public TeamUserVo(String teamUuid, String userUuid) {
		this.teamUuid = teamUuid;
		this.userUuid = userUuid;
	}

	public String getTeamUuid() {
		return teamUuid;
	}
	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}
	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
