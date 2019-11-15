package codedriver.framework.dto.auth;

import java.util.List;

import codedriver.framework.common.dto.BasePageVo;

public class AuthGroupVo extends BasePageVo{
	private String name;
	private String desc;
	private String intro;
	private Integer isActive = 1;
	private String moduleName;
	private String roleName;
	private String apiComponentId;
	
	private List<String> roleList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getApiComponentId() {
		return apiComponentId;
	}

	public void setApiComponentId(String apiComponentId) {
		this.apiComponentId = apiComponentId;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

	
}
