package codedriver.framework.scheduler.dto;

public class ServerNewJobVo {
	private Long id;
	private Integer serverId;
	private byte[] jobObject;
	
	public ServerNewJobVo() {
	}
	public ServerNewJobVo(Integer serverId, byte[] jobObject) {
		this.serverId = serverId;
		this.jobObject = jobObject;
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
	public byte[] getJobObject() {
		return jobObject;
	}
	public void setJobObject(byte[] jobObject) {
		this.jobObject = jobObject;
	}
}
