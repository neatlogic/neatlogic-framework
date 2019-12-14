package codedriver.framework.scheduler.dto;

import java.util.List;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class JobVo extends JobBaseVo {
			
	@EntityField(name = "定时作业名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "是否激活(no:禁用，yes：激活)", type = ApiParamType.STRING)
	private String isActive;
	@EntityField(name = "定时作业状态", type = ApiParamType.JSONOBJECT)
	private JobStatusVo jobStatus;
	@EntityField(name = "定时作业属性列表", type = ApiParamType.JSONARRAY)
	private List<JobPropVo> propList;

	public JobVo() {
		this.setPageSize(20);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public JobStatusVo getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobStatusVo jobStatus) {
		this.jobStatus = jobStatus;
	}

}
