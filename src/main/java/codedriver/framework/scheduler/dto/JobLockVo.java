package codedriver.framework.scheduler.dto;

public class JobLockVo {
	public final static String GET_LOCK = "run";
	public final static String RELEASE_LOCK = "wait";
	
	private Long jobId;
	private String lock;
	private Integer serverId;
	public JobLockVo() {
	}
	public JobLockVo(String lock, Integer serverId) {
		this.lock = lock;
		this.serverId = serverId;
	}
	public JobLockVo(Long jobId, String lock) {
		this.jobId = jobId;
		this.lock = lock;
	}
	public JobLockVo(Long jobId, String lock, Integer serverId) {
		this.jobId = jobId;
		this.lock = lock;
		this.serverId = serverId;
	}
	public Long getJobId() {
		return jobId;
	}
	public void setJobId(Long jobId) {
		this.jobId = jobId;
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
