package codedriver.framework.dashboard.dto;

public class DashboardVisitCounterVo {
	private String dashboardUuid;
	private String userUuid;
	private Integer visitCount = 1;

	public DashboardVisitCounterVo() {

	}

	public DashboardVisitCounterVo(String _dashboardUuid, String _userUuid) {
		dashboardUuid = _dashboardUuid;
		userUuid = _userUuid;
	}

	public String getDashboardUuid() {
		return dashboardUuid;
	}

	public void setDashboardUuid(String dashboardUuid) {
		this.dashboardUuid = dashboardUuid;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public Integer getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(Integer visitCount) {
		this.visitCount = visitCount;
	}

}
