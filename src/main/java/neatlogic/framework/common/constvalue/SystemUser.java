/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.common.constvalue;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: SystemUser
 * @Description: sla转交策略的定时作业执行转交逻辑时，需要验证权限，system用户拥有流程流转的所有权限
 */
public enum SystemUser {
    SYSTEM("system", "system", new I18n("common.system")),
    ANONYMOUS("anonymous", "anonymous", new I18n("enum.framework.systemuser.anonymous"));

    private final Logger logger = LoggerFactory.getLogger(SystemUser.class);

    private final String userId;
    private final String userUuid;
    private final I18n userName;
    private final String timezone = "+8:00";

    SystemUser(String userId, String userUuid, I18n userName) {
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
        return I18nUtils.getMessage(userName.toString());
    }

    public String getTimezone() {
        return timezone;
    }

    public UserVo getUserVo() {
        UserVo userVo = new UserVo();
        userVo.setUuid(userUuid);
        userVo.setUserId(userId);
        userVo.setUserName(getUserName());
        userVo.setTenant(TenantContext.get().getTenantUuid());
        try {
            JwtVo jwtVo = LoginAuthHandlerBase.buildJwt(userVo);
            String authorization = "Bearer_" + jwtVo.getJwthead() + "." + jwtVo.getJwtbody() + "." + jwtVo.getJwtsign();
            userVo.setAuthorization(authorization);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return userVo;
    }

    public static String getUserName(String userUuid) {
        for (SystemUser user : values()) {
            if (user.getUserUuid().equals(userUuid)) {
                return user.getUserName();
            }
        }
        return "";
    }
}
