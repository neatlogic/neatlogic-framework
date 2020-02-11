package codedriver.framework.restful.dto;

import java.util.List;

public class ApiHandlerVo {
	private String handler;
	private String name;
	private String config;
	private Integer isActive;
	private String moduleId;
	private boolean isPrivate;
	private String type;
	private List<ApiVo> interfaceList;

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public List<ApiVo> getInterfaceList() {
		return interfaceList;
	}

	public void setInterfaceList(List<ApiVo> interfaceList) {
		this.interfaceList = interfaceList;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
