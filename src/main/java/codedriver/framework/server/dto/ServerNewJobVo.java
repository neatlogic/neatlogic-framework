package codedriver.framework.server.dto;

public class ServerNewJobVo {

	private Integer serverId;
	private Long jobId;
	private String tenantUuid;
	public Integer getServerId() {
		return serverId;
	}
	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}
	public Long getJobId() {
		return jobId;
	}
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	public String getTenantUuid() {
		return tenantUuid;
	}
	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}
}
