package neatlogic.framework.dto;

import com.alibaba.fastjson.JSON;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class UserSessionVo implements Serializable {
    private String userUuid;
    private Date sessionTime;
    @EntityField(name = "权限字符串", type = ApiParamType.STRING)
    private String authInfoStr;
    @EntityField(name = "权限字符串", type = ApiParamType.STRING)
    private String authInfoHash;
    @EntityField(name = "权限", type = ApiParamType.STRING)
    private AuthenticationInfoVo authInfo;

    @EntityField(name = "token创建时间", type = ApiParamType.LONG)
    private Long tokenCreateTime;

    @EntityField(name = "token 哈希", type = ApiParamType.STRING)
    private String tokenHash;

    @EntityField(name = "token", type = ApiParamType.STRING)
    private String token;

    public UserSessionVo(String userUuid, Date sessionTime) {
        this.userUuid = userUuid;
        this.sessionTime = sessionTime;
    }

    public UserSessionVo() {
        super();
    }

    public UserSessionVo(String uuid, String token, String tokenHash, Long tokenCreateTime, String authInfoHash, String authInfoStr) {
        this.userUuid = uuid;
        this.token = token;
        this.tokenHash = tokenHash;
        this.tokenCreateTime = tokenCreateTime;
        this.authInfoHash = authInfoHash;
        this.authInfoStr = authInfoStr;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Date getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(Date sessionTime) {
        this.sessionTime = sessionTime;
    }

    public AuthenticationInfoVo getAuthInfo() {
        if (authInfo == null) {
            if (StringUtils.isNotBlank(authInfoStr)) {
                authInfo = JSON.toJavaObject(JSON.parseObject(authInfoStr), AuthenticationInfoVo.class);
            } else {
                authInfo = new AuthenticationInfoVo(userUuid);
            }
        }
        return authInfo;
    }

    public String getAuthInfoStr() {
        return authInfoStr;
    }

    public void setAuthInfoStr(String authInfoStr) {
        if(StringUtils.isBlank(authInfoStr)){
            authInfoStr = "{}";
        }
        this.authInfoStr = authInfoStr;
    }

    public Long getTokenCreateTime() {
        return tokenCreateTime;
    }

    public void setTokenCreateTime(Long tokenCreateTime) {
        this.tokenCreateTime = tokenCreateTime;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public String getAuthInfoHash() {
        return authInfoHash;
    }

    public void setAuthInfoHash(String authInfoHash) {
        this.authInfoHash = authInfoHash;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSessionVo task = (UserSessionVo) o;
        return Objects.equals(token, task.token) ;
    }
}
