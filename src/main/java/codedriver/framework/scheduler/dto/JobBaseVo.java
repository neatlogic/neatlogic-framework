package codedriver.framework.scheduler.dto;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.dto.BasePageVo;

public class JobBaseVo extends BasePageVo  {
	public final static String YES = "yes";
	public final static String NO = "no";
	private String uuid;	
	private String classpath;
	private String needAudit;
	private String cron;
	private Date beginTime;
	private Date endTime;
	public synchronized String getUuid() {
		if(StringUtils.isBlank(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getClasspath() {
		return classpath;
	}
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	public String getNeedAudit() {
		return needAudit;
	}
	public void setNeedAudit(String needAudit) {
		this.needAudit = needAudit;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
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
}
