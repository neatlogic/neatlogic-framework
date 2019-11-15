package codedriver.framework.dto.auth;

import java.util.List;

import codedriver.framework.dto.PageVo;

public class LabelVo extends PageVo{
	private String label;
	private String desc;
	private String labelIntro;
	private Integer labelIsActive = 1;
	private String moduleName;
	private String roleName;
	private String apiComponentId;
	private Integer urlIsActive = 1;
	
	private List<String> roleList;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getLabelIntro() {
		return labelIntro;
	}

	public void setLabelIntro(String labelIntro) {
		this.labelIntro = labelIntro;
	}

	public Integer getLabelIsActive() {
		return labelIsActive;
	}

	public void setLabelIsActive(Integer labelIsActive) {
		this.labelIsActive = labelIsActive;
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

	public Integer getUrlIsActive() {
		return urlIsActive;
	}

	public void setUrlIsActive(Integer urlIsActive) {
		this.urlIsActive = urlIsActive;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}
	
	
}
