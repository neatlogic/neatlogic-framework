package codedriver.framework.dto;

import java.util.List;

public class TenantModuleVo {
	private String tenantUuid;
	private List<ModuleVo> moduleList;
	
	public String getTenantUuid() {
		return tenantUuid;
	}
	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}
	public List<ModuleVo> getModuleList() {
		return moduleList;
	}
	public void setModuleList(List<ModuleVo> moduleList) {
		this.moduleList = moduleList;
	}

	

}
