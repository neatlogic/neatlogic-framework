package codedriver.framework.dto;

import java.io.Serializable;
import java.util.List;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class RoleVo extends BasePageVo implements Serializable {

	private static final long serialVersionUID = -8007028390813552667L;

	public static final String USER_DEFAULT_ROLE = "R_SYSTEM_USER";
	private transient String keyword;
	@EntityField(name = "角色名称",
			type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "角色描述",
			type = ApiParamType.STRING)
	private String description;
	private int userCount;
	private String authGroup;
	private String auth;
	private List<RoleAuthVo> roleAuthList;

	public List<RoleAuthVo> getRoleAuthList() {
		return roleAuthList;
	}

	public void setRoleAuthList(List<RoleAuthVo> roleAuthList) {
		this.roleAuthList = roleAuthList;
	}

	public String getAuthGroup() {
		return authGroup;
	}

	public void setAuthGroup(String authGroup) {
		this.authGroup = authGroup;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

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

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
