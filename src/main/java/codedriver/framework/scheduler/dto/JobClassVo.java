package codedriver.framework.scheduler.dto;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class JobClassVo extends BasePageVo {
	
	public final static String FLOW_TYPE = "flow";//flow级别
	public final static String TASK_TYPE = "task";//task级别
	public final static String ONCE_TYPE = "once";//只允许配一次
	@EntityField(name = "定时作业组件类型", type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "定时作业组件名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "定时作业组件全类名", type = ApiParamType.STRING)
	private String classpath;
	@EntityField(name = "时作业组件所属模块id", type = ApiParamType.STRING)
	private String moduleId;
	@EntityField(name = "定时作业组件所属模块名", type = ApiParamType.STRING)
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
