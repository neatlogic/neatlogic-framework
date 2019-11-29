package codedriver.framework.scheduler.dto;

import java.io.Serializable;
import java.util.Date;

import codedriver.framework.asynchronization.threadlocal.TenantContext;

public class JobObject implements Serializable {	
	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = -8409651508383155447L;
	public final static String FRAMEWORK = "FRAMEWORK";
	public final static String DELIMITER = "(_)";
	private String jobId;
	private String jobGroup;
	private String cron;
	private Date startTime;
	private Date endTime;
	private String jobClassName;
	private String needAudit;
//	private String TenantUuid;
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

	public String getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(String needAudit) {
		this.needAudit = needAudit;
	}

//	public String getTenantUuid() {
//		return TenantUuid;
//	}
//
//	public void setTenantUuid(String tenantUuid) {
//		TenantUuid = tenantUuid;
//	}

	public static JobObject buildJobObject(JobBaseVo jobBaseVo, String groupName) {
		TenantContext tenant = TenantContext.get();
		tenant.setUseDefaultDatasource(false);
		JobObject jobObject = new JobObject();
		jobObject.setJobId(jobBaseVo.getUuid());
//		jobObject.setTenantUuid(tenant.getTenantUuid());
		jobObject.setJobGroup(tenant.getTenantUuid() + DELIMITER + groupName);
		jobObject.setCron(jobBaseVo.getCron());
		jobObject.setEndTime(jobBaseVo.getEndTime());
		jobObject.setStartTime(jobBaseVo.getBeginTime());
		jobObject.setJobClassName(jobBaseVo.getClasspath());
		jobObject.setNeedAudit(jobBaseVo.getNeedAudit());
		return jobObject;
	}
}
