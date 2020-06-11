package codedriver.framework.scheduler.dto;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class JobStatusVo {

	@EntityField(name = "定时作业名称",
			type = ApiParamType.STRING)
	private String jobName;
	@EntityField(name = "定时作业组名",
			type = ApiParamType.STRING)
	private String jobGroup;
	@EntityField(name = "定时作业类名",
			type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "下一次被唤醒时间",
			type = ApiParamType.LONG)
	private Date nextFireTime;
	@EntityField(name = "最后一次被唤醒时间",
			type = ApiParamType.LONG)
	private Date lastFireTime;
	@EntityField(name = "最后一次完成时间",
			type = ApiParamType.LONG)
	private Date lastFinishTime;
	@EntityField(name = "执行次数",
			type = ApiParamType.INTEGER)
	private Integer execCount = 0;

	@JSONField(serialize = false)
	private transient Integer needAudit;

	public JobStatusVo() {
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
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

	public Integer getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(Integer needAudit) {
		this.needAudit = needAudit;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}
}
