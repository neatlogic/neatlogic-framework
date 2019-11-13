package codedriver.framework.restful.dto;

import java.util.List;

public class ApiComponentVo {
	private String id;
	private String name;
	private String config;
	private Integer isActive;
	private String module;
	
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

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
}
