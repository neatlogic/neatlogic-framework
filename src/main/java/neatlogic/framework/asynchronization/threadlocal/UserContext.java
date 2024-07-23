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

package neatlogic.framework.asynchronization.threadlocal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.user.NoUserException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
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
    private AuthenticationInfoVo authenticationInfoVo;
    //是否超级管理员
    private Boolean isSuperAdmin = false;

    private String tokenHash;

    private JwtVo jwtVo;

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
            context.setAuthenticationInfoVo(_userContext.getAuthenticationInfoVo());
            context.setJwtVo(_userContext.getJwtVo());
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
        List<String> roleUuidList = new ArrayList<>();
        JSONArray roleList = jsonObj.getJSONArray("rolelist");
        if (roleList != null && !roleList.isEmpty()) {
            for (int i = 0; i < roleList.size(); i++) {
                roleUuidList.add(roleList.getString(i));
            }
        }
        context.setAuthenticationInfoVo(new AuthenticationInfoVo(context.getUserUuid(), new ArrayList<>(), roleUuidList, new HashSet<>(), null));
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
        if (userVo.getJwtVo() != null) {
            context.setTokenHash(userVo.getJwtVo().getTokenHash());
        }
        context.setJwtVo(userVo.getJwtVo());
        instance.set(context);
        return context;
    }

    public static UserContext init(UserVo userVo, AuthenticationInfoVo authenticationInfoVo, String timezone) {
        return init(userVo, authenticationInfoVo, timezone, null, null);
    }

    public static UserContext init(SystemUser systemUser) {
        return init(systemUser.getUserVo(), systemUser.getAuthenticationInfoVo(), systemUser.getTimezone(), null, null);
    }

    public static UserContext init(SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        return init(systemUser.getUserVo(), systemUser.getAuthenticationInfoVo(), systemUser.getTimezone(), request, response);
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
