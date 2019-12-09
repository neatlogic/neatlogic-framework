package codedriver.framework.scheduler.dto;

import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.dto.BasePageVo;

public class JobClassVo extends BasePageVo {
	
	public final static String FLOW_TYPE = "flow";//flow级别
	public final static String TASK_TYPE = "task";//task级别
	public final static String ONCE_TYPE = "once";//只允许配一次
	
	private String type;	
	private String name;
	private String classpath;	
	private String moduleId;
	private String moduleName;
	@JSONField(serialize = false)
	private String tenantUuid;
	public JobClassVo() {
		this.setPageSize(20);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getTenantUuid() {
		return tenantUuid;
	}

	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}

}
