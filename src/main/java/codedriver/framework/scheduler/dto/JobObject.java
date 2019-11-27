package codedriver.framework.scheduler.dto;

import java.util.Date;
import java.util.List;

import org.quartz.JobDataMap;

import codedriver.framework.asynchronization.threadlocal.TenantContext;

public class JobObject {
	private String jobId;
	private String jobGroup;
	private int repeat;
	private int interval;
	private String cron;
	private Date startTime;
	private Date endTime;
	private String jobClassName;
	private Integer serverId;
	private String triggerType;
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

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
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
	
	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public static JobObject buildJobObject(JobVo jobVo) {
		JobObject jobObject = new JobObject();
		jobObject.setJobId(jobVo.getUuid());
		jobObject.setJobGroup(TenantContext.get().getTenantUuid());
		jobObject.setCron(jobVo.getCron());
		jobObject.setEndTime(jobVo.getEndTime());
		jobObject.setStartTime(jobVo.getBeginTime());
		jobObject.setInterval(jobVo.getInterval() == null ? 0 : jobVo.getInterval());
		jobObject.setRepeat(jobVo.getRepeat() == null ? -1 : jobVo.getRepeat());
		jobObject.setJobClassName(jobVo.getClasspath());
		jobObject.setTriggerType(jobVo.getTriggerType());
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("execCount",jobVo.getExecCount());
		List<JobPropVo> propList = jobVo.getPropList();
		if(propList != null && propList.size() > 0) {		
			for(JobPropVo prop : propList) {
				jobDataMap.put(prop.getName(), prop.getValue());
			}		
		}
		jobObject.setJobDataMap(jobDataMap);
		return jobObject;
	}
}
