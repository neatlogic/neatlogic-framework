package codedriver.framework.dto;

public class RoleUserVo {

	private Long id;
	private String roleUuid;
	private String userUuid;
	public RoleUserVo() {
	}
	public RoleUserVo(String roleUuid) {
		this.roleUuid = roleUuid;
	}
	public RoleUserVo(String roleUuid, String userUuid) {
		this.roleUuid = roleUuid;
		this.userUuid = userUuid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleUuid() {
		return roleUuid;
	}
	public void setRoleUuid(String roleUuid) {
		this.roleUuid = roleUuid;
	}
	public String getUserUuid() {
		return userUuid;
	}
	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}
}
