package codedriver.framework.scheduler.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class JobVo extends BasePageVo {

	@EntityField(name = "定时作业名称",
			type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "是否激活(0:禁用，1：激活)",
			type = ApiParamType.INTEGER)
	private Integer isActive;
	@EntityField(name = "定时作业属性列表",
			type = ApiParamType.JSONARRAY)
	private List<JobPropVo> propList;
	@EntityField(name = "定时作业uuid",
			type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "定时作业组件类路径",
			type = ApiParamType.STRING)
	private String className;
	@EntityField(name = "是否保存执行记录(0:不保存，1:保存)",
			type = ApiParamType.INTEGER)
	private Integer needAudit;
	@EntityField(name = "cron表达式",
			type = ApiParamType.STRING)
	private String cron;
	@EntityField(name = "开始时间",
			type = ApiParamType.LONG)
	private Date beginTime;
	@EntityField(name = "结束时间",
			type = ApiParamType.LONG)
	private Date endTime;

	private transient String keyword;

	public JobVo() {
		this.setPageSize(20);
	}

	public List<JobPropVo> getPropList() {
		if (propList == null || propList.size() == 0) {
			return null;
		}
		return propList;
	}

	public void setPropList(List<JobPropVo> propList) {
		this.propList = propList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getUuid() {
		if (StringUtils.isBlank(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(Integer needAudit) {
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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
