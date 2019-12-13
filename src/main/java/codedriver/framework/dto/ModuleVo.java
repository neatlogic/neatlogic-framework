package codedriver.framework.dto;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class ModuleVo extends BasePageVo {
	@EntityField( name = "模块ID", type = ApiParamType.STRING)
	private String id;
	@EntityField( name = "模块ID", type = ApiParamType.STRING)
	private String name;
	@EntityField( name = "模块描述", type = ApiParamType.STRING)
	private String description;
	@EntityField( name = "模块版本", type = ApiParamType.STRING)
	private String version;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
