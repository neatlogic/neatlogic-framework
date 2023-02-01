package neatlogic.framework.restful.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.List;

public class ApiHandlerVo {
	@EntityField(name = "处理器", type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "配置信息，json格式", type = ApiParamType.JSONOBJECT)
	private String config;
	@EntityField(name = "状态", type = ApiParamType.INTEGER)
	private Integer isActive;
	@EntityField(name = "模块ID", type = ApiParamType.STRING)
	private String moduleId;
	@EntityField(name = "是否是私有接口", type = ApiParamType.BOOLEAN)
	private boolean isPrivate;
	@EntityField(name = "处理器类型", type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "处理器token", type = ApiParamType.STRING)
	private String token;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
