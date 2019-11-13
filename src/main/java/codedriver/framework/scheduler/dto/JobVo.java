package codedriver.framework.scheduler.dto;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import codedriver.framework.common.dto.BasePageVo;

public class JobVo extends BasePageVo {
	private Long id;
	private Integer repeat;
	private Integer status;
	private Integer classId;
	private Integer interval;
	private Integer isActive;
	private Integer needAudit;
	private Integer moduleId;
	private Integer isPrivate = 0;
	private Integer triggerType;
	private Integer serverId;
	private int execCount;
	private String name;
	private String cron;
	private String sort;
	private String intervalUnit;
	private String intervalText;
	private Date beginTime;
	private Date endTime;
	private Date pauseTime;
	private Date nextFireTime;
	private Date lastFireTime;
	private Date lastFinishTime;
	private JobClassVo jobClass;
	private List<JobPropVo> propList;

	public JobVo() {
		this.setPageSize(20);
	}

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public List<JobPropVo> getPropList() {
		return propList;
	}

	public void setPropList(List<JobPropVo> propList) {
		this.propList = propList;
	}

	public Integer getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Integer isPrivate) {
		this.isPrivate = isPrivate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getExecCount() {
		return execCount;
	}

	public void setExecCount(int execCount) {
		this.execCount = execCount;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public Integer getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(Integer triggerType) {
		this.triggerType = triggerType;
	}

	public String getIntervalUnit() {
		return intervalUnit;
	}

	public void setIntervalUnit(String intervalUnit) {
		this.intervalUnit = intervalUnit;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getClassId() {
		return classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public JobClassVo getJobClass() {
		return jobClass;
	}

	public void setJobClass(JobClassVo jobClass) {
		this.jobClass = jobClass;
	}

	public Integer getRepeat() {
		return repeat;
	}

	public void setRepeat(Integer repeat) {
		this.repeat = repeat;
	}

	public Integer getInterval() {
		if (intervalUnit != null && !"".equals(intervalUnit)) {
			if (intervalUnit.equalsIgnoreCase("m")) {
				interval = interval * 60;
			} else if (intervalUnit.equalsIgnoreCase("h")) {
				interval = interval * 60 * 60;
			}
		}
		return interval;
	}

	public String getIntervalText() {
		intervalText = "";
		if (interval != null) {
			float i = interval;
			DecimalFormat df = new DecimalFormat("#.00");
			if (interval > 36000) {
				intervalText = df.format(i / 60 / 60) + "小时";
			} else if (interval > 600) {
				intervalText = df.format(i / 60) + "分钟";
			} else {
				intervalText = interval + "秒";
			}
		}
		return intervalText;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(Integer needAudit) {
		this.needAudit = needAudit;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getPauseTime() {
		return pauseTime;
	}

	public void setPauseTime(Date pauseTime) {
		this.pauseTime = pauseTime;
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

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
}
