package codedriver.framework.dto;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.RC4Util;

public class DatasourceVo {
	private Long tenantId;
	private String tenantUuid;
	private String url;
	private String username;
	private String passwordPlain;
	private String passwordCipher;
	private String driver = "com.mysql.jdbc.Driver";

	public DatasourceVo() {

	}

	public DatasourceVo(Long _tenantId, String _tenantUuid, boolean generatePwd) {
		this.tenantId = _tenantId;
		this.tenantUuid = _tenantUuid;
		// 生成随机密码
		if (generatePwd) {
			// this.passwordPlain = "123456";
			Random rand = new Random();
			String password = "";
			for (int i = 0; i < 6; i++) {
				int randNumber = rand.nextInt(126 - 48 + 1) + 48;
				char c = (char) randNumber;
				password += c;
			}
			this.passwordPlain = password;
		}
	}

	public String getTenantUuid() {
		return tenantUuid;
	}

	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}

	public String getUrl() {
		if (StringUtils.isBlank(url)) {
			url = "jdbc:mysql://" + Config.DB_HOST() + ":" + Config.DB_PORT() + "/codedriver_" + this.tenantUuid + "?characterEncoding=UTF-8&jdbcCompliantTruncation=false&allowMultiQueries=true";
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		if (StringUtils.isBlank(username)) {
			username = this.tenantUuid;
		}
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordPlain() {
		if (StringUtils.isBlank(passwordPlain)) {
			if (StringUtils.isNotBlank(passwordCipher)) {
				if (passwordCipher.startsWith("RC4:")) {
					this.passwordPlain = RC4Util.decrypt(Config.RC4KEY, this.passwordCipher.substring(4));
				} else {
					this.passwordPlain = this.passwordCipher;
				}
			}
		}
		return passwordPlain;
	}

	public void setPasswordPlain(String passwordPlain) {
		this.passwordPlain = passwordPlain;
	}

	public String getPasswordCipher() {
		if (StringUtils.isBlank(passwordCipher)) {
			if (StringUtils.isNotBlank(passwordPlain)) {
				this.passwordCipher = "RC4:" + RC4Util.encrypt(Config.RC4KEY, passwordPlain);
			}
		}
		return passwordCipher;
	}

	public void setPasswordCipher(String passwordCipher) {
		this.passwordCipher = passwordCipher;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

}
