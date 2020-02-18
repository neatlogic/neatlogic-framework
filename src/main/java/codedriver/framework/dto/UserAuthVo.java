package codedriver.framework.dto;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class UserAuthVo extends BasePageVo {
    @EntityField(name = "用户ID", type = ApiParamType.STRING)
    private String userId;
    @EntityField(name = "权限", type = ApiParamType.STRING)
    private String auth;
    @EntityField(name = "权限组", type = ApiParamType.STRING)
    private String authGroup;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getAuthGroup() {
        return authGroup;
    }

    public void setAuthGroup(String authGroup) {
        this.authGroup = authGroup;
    }
}
