package neatlogic.framework.dto;

import java.util.List;

import neatlogic.framework.common.dto.BasePageVo;

public class AuthGroupVo extends BasePageVo{
	private String displayName;
	private String name;
	private List<AuthVo> authVoList;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AuthVo> getAuthVoList() {
		return authVoList;
	}

	public void setAuthVoList(List<AuthVo> authVoList) {
		this.authVoList = authVoList;
	}
}
