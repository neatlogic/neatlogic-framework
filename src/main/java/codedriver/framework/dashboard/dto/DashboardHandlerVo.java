package codedriver.framework.dashboard.dto;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class DashboardHandlerVo {
	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "显示名称", type = ApiParamType.STRING)
	private String displayName;
	@EntityField(name = "处理类", type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "类型", type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "图标", type = ApiParamType.STRING)
	private String icon;
	@EntityField(name = "所属模块", type = ApiParamType.STRING)
	private String moduleId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
