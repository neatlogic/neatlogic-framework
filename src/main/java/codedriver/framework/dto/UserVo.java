package codedriver.framework.dto;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

public class UserVo {
	private String userId;
	private String userName;
	private String tenant;
	private String email;
	private String password;
	private List<String> roleList;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		if (StringUtils.isNotBlank(password)) {
			if (!password.startsWith("{MD5}")) {
				password = DigestUtils.md5DigestAsHex(password.getBytes());
				password = "{MD5}" + password;
			}
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

}
