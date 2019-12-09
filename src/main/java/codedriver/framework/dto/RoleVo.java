package codedriver.framework.dto;

import java.io.Serializable;

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
