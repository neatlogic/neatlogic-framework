package codedriver.framework.scheduler.dto;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class JobClassVo extends BasePageVo {
	public final static String PUBLIC = "public";
	public final static String PRIVATE = "private";
	@EntityField(name = "定时作业组件名称",
			type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "作业组件类型：public（公开）|private（私有），公开组件允许用户自行配作业",
			type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "定时作业组件全类名",
			type = ApiParamType.STRING)
	private String className;
	@EntityField(name = "时作业组件所属模块id",
			type = ApiParamType.STRING)
	private String moduleId;

	private transient String keyword;

	public JobClassVo() {
		this.setPageSize(20);
	}

	public JobClassVo(String classname, String moduleId) {
		this.setPageSize(20);
		this.className = classname;
		this.moduleId = moduleId;
	}

	public String getModuleId() {
		return moduleId;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setClassName(String classpath) {
		this.className = classpath;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
