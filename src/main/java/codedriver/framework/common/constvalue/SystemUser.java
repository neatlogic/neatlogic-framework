package codedriver.framework.common.constvalue;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dto.JwtVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.filter.core.LoginAuthHandlerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
* @Author:14378
* @Time:2020年7月3日
* @ClassName: SystemUser 
* @Description: sla转交策略的定时作业执行转交逻辑时，需要验证权限，system用户拥有流程流转的所有权限
 */
public enum SystemUser {
	SYSTEM("system","system","系统"),
	ANONYMOUS("anonymous","anonymous","匿名用户");

	private final Logger logger = LoggerFactory.getLogger(SystemUser.class);

	private String userId;
	private String userUuid;
	private String userName;
	private String timezone = "+8:00";
	private SystemUser(String userId, String userUuid, String userName) {
		this.userId = userId;
		this.userUuid = userUuid;
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public String getUserUuid() {
		return userUuid;
	}
	public String getUserName() {
		return userName;
	}
	
	public String getTimezone() {
		return timezone;
	}
	public UserVo getUserVo() {
		UserVo userVo = new UserVo();
		userVo.setUuid(userUuid);
		userVo.setUserId(userId);
		userVo.setUserName(userName);
		userVo.setTenant(TenantContext.get().getTenantUuid());
		try {
			JwtVo jwtVo = LoginAuthHandlerBase.buildJwt(userVo);
			String authorizatioin = "Bearer_" + jwtVo.getJwthead() + "." + jwtVo.getJwtbody() + "." + jwtVo.getJwtsign();
			userVo.setAuthorization(authorizatioin);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return userVo;
	};
	
	public static String getUserName(String userUuid) {
		for(SystemUser user : values()) {
			if(user.getUserUuid().equals(userUuid)) {
				return user.getUserName();
			}
		}
		return "";
	}
}
