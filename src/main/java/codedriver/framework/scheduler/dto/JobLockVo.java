package codedriver.framework.scheduler.dto;

public class JobLockVo {
	public final static String RUN = "run";
	public final static String WAIT = "wait";
	
	private String jobUuid;
	private String lock;
	private Integer serverId;
	public JobLockVo() {
	}
	public JobLockVo(String lock, Integer serverId) {
		this.lock = lock;
		this.serverId = serverId;
	}
	public JobLockVo(String jobUuid, String lock) {
		this.jobUuid = jobUuid;
		this.lock = lock;
	}
	public JobLockVo(String jobUuid, String lock, Integer serverId) {
		this.jobUuid = jobUuid;
		this.lock = lock;
		this.serverId = serverId;
	}
	public String getJobUuid() {
		return jobUuid;
	}
	public void setJobUuid(String jobUuid) {
		this.jobUuid = jobUuid;
	}
	public String getLock() {
		return lock;
	}
	public void setLock(String lock) {
		this.lock = lock;
	}
	public Integer getServerId() {
		return serverId;
	}
	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}
}
