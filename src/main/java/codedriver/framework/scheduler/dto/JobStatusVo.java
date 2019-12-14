package codedriver.framework.scheduler.dto;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class JobStatusVo {

	public final static String RUNNING = "running";
	public final static String STOP = "stop";
	public final static String NOT_LOADED = "not_loaded";
	
	@EntityField(name = "定时作业uuid", type = ApiParamType.STRING)
	private String jobUuid;
	@EntityField(name = "定时作业组名", type = ApiParamType.STRING)
	private String jobGroup;
	@EntityField(name = "状态(running:运行中;stop:停止;not_loaded未加载)", type = ApiParamType.STRING)
	private String status;
	@EntityField(name = "下一次被唤醒时间", type = ApiParamType.LONG)
	private Date nextFireTime;
	@EntityField(name = "最后一次被唤醒时间", type = ApiParamType.LONG)
	private Date lastFireTime;
	@EntityField(name = "最后一次完成时间", type = ApiParamType.LONG)
	private Date lastFinishTime;
	@EntityField(name = "执行次数", type = ApiParamType.INTEGER)
	private Integer execCount;
	
	@JSONField(serialize = false)
	private transient String needAudit;
	
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
