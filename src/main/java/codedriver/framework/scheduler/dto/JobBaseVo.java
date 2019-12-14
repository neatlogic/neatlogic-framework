package codedriver.framework.scheduler.dto;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class JobBaseVo extends BasePageVo  {
	public final static String YES = "yes";
	public final static String NO = "no";
	@EntityField(name = "定时作业uuid", type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "定时作业组件类路径", type = ApiParamType.STRING)
	private String classpath;
	@EntityField(name = "是否保存执行记录(no:不保存，yes:保存)", type = ApiParamType.STRING)
	private String needAudit;
	@EntityField(name = "cron表达式", type = ApiParamType.STRING)
	private String cron;
	@EntityField(name = "开始时间", type = ApiParamType.LONG)
	private Date beginTime;
	@EntityField(name = "结束时间", type = ApiParamType.LONG)
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
