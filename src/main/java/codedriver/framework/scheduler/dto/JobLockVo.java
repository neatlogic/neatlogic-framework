package codedriver.framework.scheduler.dto;

import codedriver.framework.common.config.Config;

public class JobLockVo {
	public final static String RUNNING = "running";
	public final static String WAITING = "waiting";

	private String jobName;
	private String jobGroup;
	private String jobClassName;
	private String lock = WAITING;
	private Integer serverId;

	public JobLockVo() {

	}

	public JobLockVo(String _jobName, String _jobGroup, String _jobClassName) {
		jobName = _jobName;
		jobGroup = _jobGroup;
		jobClassName = _jobClassName;
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}

	public Integer getServerId() {
		if (serverId == null) {
			serverId = Config.SCHEDULE_SERVER_ID;
		}
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
}
