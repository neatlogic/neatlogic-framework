package codedriver.framework.dto;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.RC4Util;

public class DatasourceVo {
	private String tenantUuid;
	private String url;
	private String username;
	private String passwordPlain;
	private String passwordCipher;
	private String driver;

	public DatasourceVo() {

	}

	public DatasourceVo(String _tenantUuid) {
		this.tenantUuid = _tenantUuid;
	}

	public String getTenantUuid() {
		return tenantUuid;
	}

	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}

	public String getUrl() {
		if (StringUtils.isBlank(url)) {
			url = "jdbc:mysql://" + Config.DB_HOST() + ":" + Config.DB_PORT() + "/codedriver_" + this.tenantUuid + "?characterEncoding=UTF-8&jdbcCompliantTruncation=false";
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
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

}
