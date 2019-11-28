package codedriver.framework.scheduler.dto;

import java.util.Date;

import org.quartz.JobDataMap;

import codedriver.framework.asynchronization.threadlocal.TenantContext;

public class JobObject {
	public final static String FRAMEWORK = "FRAMEWORK";
	public final static String DELIMITER = "(_)";
	private String jobId;
	private String jobGroup;
	private String cron;
	private Date startTime;
	private Date endTime;
	private String jobClassName;
	private JobDataMap jobDataMap = new JobDataMap();

	public JobObject(String _jobId, String _jobGroup) {
		this.jobId = _jobId;
		this.jobGroup = _jobGroup;
	}

	public JobObject() {

	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
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

	public static JobObject buildJobObject(JobVo jobVo, String groupName) {
		JobObject jobObject = new JobObject();
		jobObject.setJobId(jobVo.getUuid());
		jobObject.setJobGroup(TenantContext.get().getTenantUuid() + DELIMITER + groupName);
		jobObject.setCron(jobVo.getCron());
		jobObject.setEndTime(jobVo.getEndTime());
		jobObject.setStartTime(jobVo.getBeginTime());
		jobObject.setJobClassName(jobVo.getClasspath());
		return jobObject;
	}
}
