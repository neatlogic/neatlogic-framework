/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.asynchronization.threadlocal;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.systemuser.ISystemUser;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.user.NoUserException;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -578199115176786224L;
    @JSONField(serialize = false)
    private static final ThreadLocal<UserContext> instance = new ThreadLocal<>();
    private String tenant;
    private String userName;
    private String userId;
    private String userUuid;
    private String timezone = TimeUtil.ZONE_TIME;
    private String token;
    private AuthenticationInfoVo authenticationInfoVo;
    //是否超级管理员
    private Boolean isSuperAdmin = false;

    private String tokenHash;

    private JwtVo jwtVo;

    public UserContext copy() {
        UserContext userContext = new UserContext();
        userContext.setToken(token);
        if (authenticationInfoVo != null) {
            userContext.setAuthenticationInfoVo(authenticationInfoVo.copy());
        }
        userContext.setIsSuperAdmin(isSuperAdmin);
        userContext.setTenant(tenant);
        userContext.setUserName(userName);
        userContext.setUserId(userId);
        userContext.setUserUuid(userUuid);
        userContext.setTimezone(timezone);
        userContext.setTokenHash(tokenHash);
        return userContext;
    }

    public static UserContext init(UserContext _userContext) {
        UserContext context = new UserContext();
        if (_userContext != null) {
            context = _userContext.copy();
        }
        instance.set(context);
        MDC.put("userId", context.getUserId());
        return context;
    }

    public static UserContext init(UserVo userVo, AuthenticationInfoVo authenticationInfoVo, String timezone) {
        UserContext context = new UserContext();
        context.setUserId(userVo.getUserId());
        context.setUserUuid(userVo.getUuid());
        context.setUserName(userVo.getUserName());
        context.setTenant(userVo.getTenant());
        String token = StringUtils.isBlank(userVo.getAuthorization()) ? userVo.getCookieAuthorization() : userVo.getAuthorization();
        if (StringUtils.isBlank(token)) {
            try {
                token = "GZIP_" + LoginAuthHandlerBase.buildJwt(userVo).getCc();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        context.setToken(token);
        context.setIsSuperAdmin(userVo.getIsSuperAdmin());
        context.setTimezone(timezone);
        context.setAuthenticationInfoVo(authenticationInfoVo);
        if (userVo.getJwtVo() != null) {
            context.setTokenHash(userVo.getJwtVo().getTokenHash());
        }
        context.setJwtVo(userVo.getJwtVo());
        instance.set(context);
        MDC.put("userId", context.getUserId());
        return context;
    }

    public static UserContext init(ISystemUser systemUser) {
        return init(systemUser.getUserVo(), systemUser.getAuthenticationInfoVo(), systemUser.getTimezone());
    }


    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    private UserContext() {

    }

    public static UserContext get() {
        return instance.get();
    }

    public void release() {
        instance.remove();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserId(boolean need) {
        if (need && StringUtils.isBlank(userId)) {
            throw new NoUserException();
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getUserUuid(boolean need) {
        if (need && StringUtils.isBlank(userUuid)) {
            //throw new NoUserException();
            return SystemUser.SYSTEM.getUserUuid();
        }
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public List<String> getRoleUuidList() {
        if (this.authenticationInfoVo != null) {
            return this.authenticationInfoVo.getRoleUuidList();
        }
        return new ArrayList<>();
    }

    public List<String> getTeamUuidList() {
        if (this.authenticationInfoVo != null) {
            return this.authenticationInfoVo.getTeamUuidList();
        }
        return new ArrayList<>();
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthenticationInfoVo getAuthenticationInfoVo() {
        if (authenticationInfoVo == null) {
            authenticationInfoVo = new AuthenticationInfoVo(userUuid);
        }
        return authenticationInfoVo;
    }

    public void setAuthenticationInfoVo(AuthenticationInfoVo authenticationInfoVo) {
        this.authenticationInfoVo = authenticationInfoVo;
    }

    /**
     * 获取用户的所有user、role、team的uuid列表
     * 用于sql校验数据权限
     *
     * @return 用户的所有user、role、team的uuid列表
     */
    public List<String> getUuidList() {
        List<String> authUuidList = new ArrayList<>();
        if (authenticationInfoVo != null) {
            authUuidList.add(authenticationInfoVo.getUserUuid());
            if (CollectionUtils.isNotEmpty(authenticationInfoVo.getTeamUuidList())) {
                authUuidList.addAll(authenticationInfoVo.getTeamUuidList());
            }
            if (CollectionUtils.isNotEmpty(authenticationInfoVo.getRoleUuidList())) {
                authUuidList.addAll(authenticationInfoVo.getRoleUuidList());
            }
        }
        return authUuidList;
    }

    public Boolean getIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(Boolean isSuperAdmin) {
        if (isSuperAdmin != null) {
            this.isSuperAdmin = isSuperAdmin;
        }
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public JwtVo getJwtVo() {
        return jwtVo;
    }

    public void setJwtVo(JwtVo jwtVo) {
        this.jwtVo = jwtVo;
    }
}
