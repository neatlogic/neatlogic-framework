package codedriver.framework.scheduler.dto;

public class ServerNewJobVo {
	private Long id;
	private Integer serverId;
	private String jobUuid;
	private String tenantUuid;
	
	public ServerNewJobVo() {
	}
	public ServerNewJobVo(Integer serverId, String jobUuid, String tenantUuid) {
		this.serverId = serverId;
		this.jobUuid = jobUuid;
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
	public String getJobUuid() {
		return jobUuid;
	}
	public void setJobUuid(String jobUuid) {
		this.jobUuid = jobUuid;
	}
	public String getTenantUuid() {
		return tenantUuid;
	}
	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}
}
