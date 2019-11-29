package codedriver.framework.scheduler.dto;

import java.util.Date;

public class JobStatusVo {

	public final static String RUNNING = "running";
	public final static String STOP = "stop";
	public final static String NOT_LOADED = "not_loaded";
	
	private String jobUuid;
	private String jobGroup;
	private String status;
	private Date nextFireTime;
	private Date lastFireTime;
	private Date lastFinishTime;
	private Integer execCount;
	private String needAudit;
	public JobStatusVo() {
	}
	public JobStatusVo(String jobUuid, String status) {
		this.jobUuid = jobUuid;
		this.status = status;
	}
	public JobStatusVo(String jobUuid, String jobGroup, String status, String needAudit) {
		this.jobUuid = jobUuid;		
		this.jobGroup = jobGroup;
		this.status = status;
		this.needAudit = needAudit;
	}
	public String getJobUuid() {
		return jobUuid;
	}
	public void setJobUuid(String jobUuid) {
		this.jobUuid = jobUuid;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getNextFireTime() {
		return nextFireTime;
	}
	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}
	public Date getLastFireTime() {
		return lastFireTime;
	}
	public void setLastFireTime(Date lastFireTime) {
		this.lastFireTime = lastFireTime;
	}
	public Date getLastFinishTime() {
		return lastFinishTime;
	}
	public void setLastFinishTime(Date lastFinishTime) {
		this.lastFinishTime = lastFinishTime;
	}
	public Integer getExecCount() {
		return execCount;
	}
	public void setExecCount(Integer execCount) {
		this.execCount = execCount;
	}
	public String getNeedAudit() {
		return needAudit;
	}
	public void setNeedAudit(String needAudit) {
		this.needAudit = needAudit;
	}
}
