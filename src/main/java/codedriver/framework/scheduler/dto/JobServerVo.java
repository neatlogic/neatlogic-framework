package codedriver.framework.scheduler.dto;

import codedriver.framework.common.config.Config;

public class JobServerVo {

	private String jobName;
	private String jobGroup;
	private String className;
	private Integer serverId;
	private Integer needReload = 0;

	public JobServerVo() {

	}

	public JobServerVo(String _jobName, String _jobGroup, String _className) {
		jobName = _jobName;
		jobGroup = _jobGroup;
		className = _className;
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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		if (serverId == null) {
			serverId = Config.SCHEDULE_SERVER_ID;
		}
		this.serverId = serverId;
	}

	public Integer getNeedReload() {
		return needReload;
	}

	public void setNeedReload(Integer needReload) {
		this.needReload = needReload;
	}

}
