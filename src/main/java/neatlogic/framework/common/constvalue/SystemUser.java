/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.common.constvalue;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @ClassName: SystemUser
 * @Description: sla转交策略的定时作业执行转交逻辑时，需要验证权限，system用户拥有流程流转的所有权限
 */
public enum SystemUser {
    SYSTEM("system", "system", new I18n("系统")),
    ANONYMOUS("anonymous", "anonymous", new I18n("匿名用户")),
    AUTOEXEC("autoexec", "autoexec", new I18n("自动化用户"));

    private final Logger logger = LoggerFactory.getLogger(SystemUser.class);

    private final String userId;
    private final String userUuid;
    private final I18n userName;
    private final String timezone = "+8:00";
    private final AuthenticationInfoVo authenticationInfoVo;

    SystemUser(String userId, String userUuid, I18n userName) {
        this.userId = userId;
        this.userUuid = userUuid;
        this.userName = userName;
        this.authenticationInfoVo = new AuthenticationInfoVo(userUuid);
    }

    public String getUserId() {
        return userId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getUserName() {
        return $.t(userName.toString());
    }

    public String getTimezone() {
        return timezone;
    }

    public String getToken() {
        if (Objects.equals(userId, AUTOEXEC.getUserId())) {
            return Config.AUTOEXEC_TOKEN();
        }
        return null;
    }

    public UserVo getUserVo() {
        UserVo userVo = new UserVo();
        userVo.setUuid(userUuid);
        userVo.setUserId(userId);
        userVo.setUserName(getUserName());
        userVo.setTenant(TenantContext.get() != null ? TenantContext.get().getTenantUuid() : null);
        userVo.setIsDelete(0);
        userVo.setIsActive(1);
        try {
            JwtVo jwtVo = LoginAuthHandlerBase.buildJwt(userVo);
            String authorization = "Bearer_" + jwtVo.getJwthead() + "." + jwtVo.getJwtbody() + "." + jwtVo.getJwtsign();
            userVo.setAuthorization(authorization);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return userVo;
    }

    public UserVo getUserVo(Boolean isNeedJwt) {
        UserVo userVo = new UserVo();
        userVo.setUuid(userUuid);
        userVo.setUserId(userId);
        userVo.setUserName(getUserName());
        userVo.setTenant(TenantContext.get() != null ? TenantContext.get().getTenantUuid() : null);
        userVo.setIsDelete(0);
        userVo.setIsActive(1);
        if (isNeedJwt) {
            try {
                JwtVo jwtVo = LoginAuthHandlerBase.buildJwt(userVo);
                String authorization = "Bearer_" + jwtVo.getJwthead() + "." + jwtVo.getJwtbody() + "." + jwtVo.getJwtsign();
                userVo.setAuthorization(authorization);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return userVo;
    }

    public AuthenticationInfoVo getAuthenticationInfoVo() {
        return authenticationInfoVo;
    }

    public static String getUserName(String userUuid) {
        for (SystemUser user : values()) {
            if (user.getUserUuid().equals(userUuid)) {
                return user.getUserName();
            }
        }
        return "";
    }

    public static UserVo getUserVoByUser(String user) {
        for (SystemUser systemUser : values()) {
            if (systemUser.getUserUuid().equals(user) || systemUser.getUserId().equals(user)) {
                return systemUser.getUserVo();
            }
        }
        return null;
    }

    public static String getUserTokenByUser(String user) {
        for (SystemUser systemUser : values()) {
            if (systemUser.getUserUuid().equals(user) || systemUser.getUserId().equals(user)) {
                return systemUser.getToken();
            }
        }
        return null;
    }
}
