package codedriver.framework.scheduler.dto;

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
	public JobClassVo() {
		this.setPageSize(20);
	}

	public JobClassVo(String type, String name, String classpath, String moduleId, String moduleName) {
		this.setPageSize(20);
		this.type = type;
		this.name = name;
		this.classpath = classpath;
		this.moduleId = moduleId;
		this.moduleName = moduleName;
	}

	public String getType() {
		return type;
	}

	public String getModuleId() {
		return moduleId;
	}

	public String getName() {
		return name;
	}

	public String getClasspath() {
		return classpath;
	}


	public String getModuleName() {
		return moduleName;
	}

}
