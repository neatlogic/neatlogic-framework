package codedriver.framework.dashboard.dto;

public class DashboardVisitCounterVo {
	private String dashboardUuid;
	private String userId;
	private Integer visitCount = 1;

	public DashboardVisitCounterVo() {

	}

	public DashboardVisitCounterVo(String _dashboardUuid, String _userId) {
		dashboardUuid = _dashboardUuid;
		userId = _userId;
	}

	public String getDashboardUuid() {
		return dashboardUuid;
	}

	public void setDashboardUuid(String dashboardUuid) {
		this.dashboardUuid = dashboardUuid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(Integer visitCount) {
		this.visitCount = visitCount;
	}

}
