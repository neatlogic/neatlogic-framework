package codedriver.framework.scheduler.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.dto.BasePageVo;

public class JobVo extends BasePageVo {
	
	public final static String RUNNING = "running";
	public final static String STOP = "stop";
	public final static String NOT_LOADED = "not_loaded";
	
	public final static String YES = "yes";
	public final static String NO = "no";
	
	public final static String SIMPLE_TRIGGER = "simple";
	public final static String CRON_TRIGGER = "cron";
	
	private String uuid;
	private Integer repeat;
	private String status;
	private String classpath;
	private Integer interval;
	private String isActive;
	private String needAudit;
	private String triggerType;
	private Integer serverId;
	private Integer execCount;
	private String name;
	private String cron;
	private Date beginTime;
	private Date endTime;
	private Date nextFireTime;
	private Date lastFireTime;
	private Date lastFinishTime;
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
		if(propList == null || propList.size() == 0 ) {
			return null;
		}
		return propList;
	}

	public void setPropList(List<JobPropVo> propList) {
		this.propList = propList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getExecCount() {
		return execCount;
	}

	public void setExecCount(Integer execCount) {
		this.execCount = execCount;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public synchronized String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		if(StringUtils.isBlank(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
		this.uuid = uuid;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
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

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(String needAudit) {
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
}
