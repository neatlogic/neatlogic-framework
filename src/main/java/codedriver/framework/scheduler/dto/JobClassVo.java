package codedriver.framework.scheduler.dto;

import codedriver.framework.common.dto.BasePageVo;

public class JobClassVo extends BasePageVo {
	private Integer id;
	private Integer type;	
	private String name;
	private String classPath;	
	private String moduleName;
	private String moduleDesc;

	public JobClassVo() {
		this.setPageSize(20);
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	@Override
	public String toString() {
		return "JobClassVo [id=" + id + ", type=" + type + ", name=" + name + ", classPath=" + classPath + ", moduleName=" + moduleName + ", moduleDesc=" + moduleDesc + "]";
	}
	
	

}
