/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.common.constvalue.systemuser;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.util.$;
import neatlogic.framework.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: SystemUser
 * @Description: sla转交策略的定时作业执行转交逻辑时，需要验证权限，system用户拥有流程流转的所有权限
 */
public enum SystemUser implements ISystemUser {
    SYSTEM("system", "system", "nfccs.systemuser.system"),
    ANONYMOUS("anonymous", "anonymous", "nfccs.systemuser.anonymous"),
    AUTOEXEC("autoexec", "autoexec", "nfccs.systemuser.autoexec");

    private final Logger logger = LoggerFactory.getLogger(SystemUser.class);

    private final String userId;
    private final String userUuid;
    private final String userName;
    private final AuthenticationInfoVo authenticationInfoVo;

    SystemUser(String userId, String userUuid, String userName) {
        this.userId = userId;
        this.userUuid = userUuid;
        this.userName = userName;
        this.authenticationInfoVo = new AuthenticationInfoVo(userUuid);
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getUserUuid() {
        return userUuid;
    }

    @Override
    public String getUserName() {
        return $.t(userName);
    }

    @Override
    public String getTimezone() {
        return TimeUtil.ZONE_TIME;
    }


    @Override
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

    @Override
    public String getToken() {
        if (Objects.equals(userId, AUTOEXEC.getUserId())) {
            return Config.AUTOEXEC_TOKEN();
        }
        return null;
    }

    @Override
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

    @Override
    public AuthenticationInfoVo getAuthenticationInfoVo() {
        return authenticationInfoVo;
    }

    @Override
    public List<ISystemUser> getSystemUserList() {
        return Arrays.asList(values());
    }
}
