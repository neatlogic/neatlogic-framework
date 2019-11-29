package codedriver.framework.scheduler.dto;

import java.util.List;

public class JobVo extends JobBaseVo {
		
	private String name;
	private String isActive;		
	private JobStatusVo jobStatus;
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
