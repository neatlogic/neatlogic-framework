package neatlogic.framework.dto;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

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

    public UserSessionVo(String userUuid, Date sessionTime) {
        this.userUuid = userUuid;
        this.sessionTime = sessionTime;
    }

    public UserSessionVo() {
        super();
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
                authInfo = JSONObject.toJavaObject(JSONObject.parseObject(authInfoStr), AuthenticationInfoVo.class);
            }else{
                authInfo = new AuthenticationInfoVo(userUuid);
            }
        }
        return authInfo;
    }

    public String getAuthInfoStr() {
        return authInfoStr;
    }

    public void setAuthInfoStr(String authInfoStr) {
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
}
