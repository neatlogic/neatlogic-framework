package codedriver.framework.scheduler.dto;

import codedriver.framework.common.dto.BasePageVo;

public class JobClassVo extends BasePageVo {
	
	public final static String FLOW_TYPE = "flow";//flow级别
	public final static String TASK_TYPE = "task";//task级别
	public final static String ONCE_TYPE = "once";//只允许配一次
	public final static String SYSTEM_TYPE = "system";//系统级别，服务器挂后，正在运行中的job将不会被转移到其他服务器
	
//	private Integer id;
	private String type;	
	private String name;
	private String classpath;	
	private String moduleName;
	private String moduleDesc;

	public JobClassVo() {
		this.setPageSize(20);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}

	public String getModuleDesc() {
		return moduleDesc;
	}

	public void setModuleDesc(String moduleDesc) {
		this.moduleDesc = moduleDesc;
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

	@Override
	public String toString() {
		return "JobClassVo [type=" + type + ", name=" + name + ", classpath=" + classpath + ", moduleName=" + moduleName + ", moduleDesc=" + moduleDesc + "]";
	}
	
	

}
