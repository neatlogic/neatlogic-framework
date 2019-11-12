package codedriver.framework.scheduler.dto;

import java.util.Date;

import org.quartz.JobDataMap;

public class JobObject {
	private Long jobId;
	private String jobGroup;
	private Integer repeat;
	private Integer interval;
	private String cron;
	private Date startTime;
	private Date endTime;
	private String jobClassName;
	private Integer serverId;
	private JobDataMap jobDataMap = new JobDataMap();

	public JobObject(Long _jobId, String _jobGroup) {
		this.jobId = _jobId;
		this.jobGroup = _jobGroup;
	}

	public JobObject() {

	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}

	public void addJobData(String key, Object value) {
		jobDataMap.put(key, value);
	}

	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public Integer getRepeat() {
		return repeat;
	}

	public void setRepeat(Integer repeat) {
		this.repeat = repeat;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public Date getStartTime() {
		return startTime;	
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;		
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}
}
