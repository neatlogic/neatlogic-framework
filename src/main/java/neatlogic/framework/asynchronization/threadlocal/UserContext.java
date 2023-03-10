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

package neatlogic.framework.asynchronization.threadlocal;

import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.user.NoUserException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserContext implements Serializable {
    private static final long serialVersionUID = -578199115176786224L;
    @JSONField(serialize = false)
    private final transient static ThreadLocal<UserContext> instance = new ThreadLocal<UserContext>();
    @JSONField(serialize = false)
    private transient HttpServletRequest request;
    @JSONField(serialize = false)
    private transient HttpServletResponse response;
    private String tenant;
    private String userName;
    private String userId;
    private String userUuid;
    private String timezone = "+8:00";
    private String token;
    private List<String> roleUuidList = new ArrayList<>();
    private AuthenticationInfoVo authenticationInfoVo;
    //是否超级管理员
    private Boolean isSuperAdmin = false;

    public static UserContext init(UserContext _userContext) {
        UserContext context = new UserContext();
        if (_userContext != null) {
            context.setUserId(_userContext.getUserId());
            context.setUserUuid(_userContext.getUserUuid());
            context.setUserName(_userContext.getUserName());
            context.setTenant(_userContext.getTenant());
            context.setTimezone(_userContext.getTimezone());
            context.setToken(_userContext.getToken());
            // context.setRequest(_userContext.getRequest());
            // context.setResponse(_userContext.getResponse());
            context.setRoleUuidList(_userContext.getRoleUuidList());
            context.setAuthenticationInfoVo(_userContext.getAuthenticationInfoVo());
            context.setIsSuperAdmin(_userContext.getIsSuperAdmin());
        }
        instance.set(context);
        return context;
    }

    public static UserContext init(JSONObject jsonObj, String token, String timezone, HttpServletRequest request, HttpServletResponse response) {
        UserContext context = new UserContext();
        context.setUserId(jsonObj.getString("userid"));
        context.setUserUuid(jsonObj.getString("useruuid"));
        context.setUserName(jsonObj.getString("username"));
        context.setTenant(jsonObj.getString("tenant"));
        context.setRequest(request);
        context.setToken(token);
        context.setResponse(response);
        context.setTimezone(timezone);
        JSONArray roleList = jsonObj.getJSONArray("rolelist");
        if (roleList != null && roleList.size() > 0) {
            for (int i = 0; i < roleList.size(); i++) {
                context.addRole(roleList.getString(i));
            }
        }
        instance.set(context);
        return context;
    }

    public static UserContext init(UserVo userVo, AuthenticationInfoVo authenticationInfoVo, String timezone, HttpServletRequest request, HttpServletResponse response) {
        UserContext context = new UserContext();
        context.setUserId(userVo.getUserId());
        context.setUserUuid(userVo.getUuid());
        context.setUserName(userVo.getUserName());
        context.setTenant(userVo.getTenant());
        context.setToken(StringUtils.isBlank(userVo.getAuthorization()) ? userVo.getCookieAuthorization() : userVo.getAuthorization());
        context.setIsSuperAdmin(userVo.getIsSuperAdmin());
        context.setRequest(request);
        context.setResponse(response);
        context.setTimezone(timezone);
        context.setAuthenticationInfoVo(authenticationInfoVo);
        for (String roleUuid : userVo.getRoleUuidList()) {
            context.addRole(roleUuid);
        }
        instance.set(context);
        return context;
    }

    public static UserContext init(UserVo userVo, String timezone, HttpServletRequest request, HttpServletResponse response) {
        return init(userVo, null, timezone, request, response);
    }

    public static UserContext init(UserVo userVo, AuthenticationInfoVo authenticationInfoVo, String timezone) {
        return init(userVo, authenticationInfoVo, timezone, null, null);
    }

    public static UserContext init(UserVo userVo, String timezone) {
        return init(userVo, null, timezone, null, null);
    }

    public void addRole(String role) {
        if (!roleUuidList.contains(role)) {
            roleUuidList.add(role);
        }
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
        if (StringUtils.isBlank(userId)) {
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
        if (StringUtils.isBlank(userUuid)) {
            //throw new NoUserException();
            return SystemUser.SYSTEM.getUserUuid();
        }
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public List<String> getRoleUuidList() {
        return roleUuidList;
    }

    public void setRoleUuidList(List<String> roleUuidList) {
        this.roleUuidList = roleUuidList;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthenticationInfoVo getAuthenticationInfoVo() {
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
        if(isSuperAdmin != null) {
            this.isSuperAdmin = isSuperAdmin;
        }
    }
}
