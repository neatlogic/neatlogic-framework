package codedriver.framework.scheduler.dto;

public class ServerNewJobVo {
	private Long id;
	private Integer serverId;
	private Long jobId;
	private String tenantUuid;
	
	public ServerNewJobVo() {
	}
	public ServerNewJobVo(Integer serverId, Long jobId, String tenantUuid) {
		this.serverId = serverId;
		this.jobId = jobId;
		this.tenantUuid = tenantUuid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
