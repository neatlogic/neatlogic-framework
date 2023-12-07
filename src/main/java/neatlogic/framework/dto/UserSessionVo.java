package neatlogic.framework.dto;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class UserSessionVo {
    private String userUuid;
    private Date sessionTime;
    @EntityField(name = "权限字符串", type = ApiParamType.STRING)
    private String authInfoStr;
    @EntityField(name = "权限", type = ApiParamType.STRING)
    private AuthenticationInfoVo authInfo;

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
        if (authInfo == null && StringUtils.isNotBlank(authInfoStr)) {
            authInfo = JSONObject.toJavaObject(JSONObject.parseObject(authInfoStr), AuthenticationInfoVo.class);
        }
        return authInfo;
    }

    public String getAuthInfoStr() {
        return authInfoStr;
    }

    public void setAuthInfoStr(String authInfoStr) {
        this.authInfoStr = authInfoStr;
    }
}
