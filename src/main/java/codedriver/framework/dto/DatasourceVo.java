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
	private String driver = "com.mysql.cj.jdbc.Driver";

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
			String[] chars = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

			for (int i = 0; i < 6; i++) {
				int randNumber = rand.nextInt(chars.length);
				password += chars[randNumber];
			}
			this.passwordPlain = password;
		}
	}

	public static void main(String[] argv) {
		System.out.println(RC4Util.encrypt(Config.RC4KEY, "123456"));
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
