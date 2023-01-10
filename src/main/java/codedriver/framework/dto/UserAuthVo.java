package codedriver.framework.dto;


import codedriver.framework.asynchronization.threadlocal.UserContext;
import org.apache.commons.lang3.StringUtils;

import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.auth.core.AuthFactory;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

import java.io.Serializable;

public class UserAuthVo extends BasePageVo implements Serializable {
    private static final long serialVersionUID = 6069104727643916207L;

    @EntityField(name = "用户Uuid", type = ApiParamType.STRING)
    private String userUuid;
    @EntityField(name = "权限", type = ApiParamType.STRING)
    private String auth;
    @EntityField(name = "权限组", type = ApiParamType.STRING)
    private String authGroup;
    @EntityField(name = "权限名", type = ApiParamType.STRING)
    private String authName;


    public UserAuthVo() {

    }

    public UserAuthVo(String _userUuid) {
        this.userUuid = _userUuid;
    }

    public UserAuthVo(String _userUuid, String _auth) {
        this.userUuid = _userUuid;
        this.auth = _auth;
    }

    public UserAuthVo(String _userUuid, AuthBase authInstance) {
        this.userUuid = _userUuid;
        this.auth = authInstance.getAuthName();
        this.authGroup = authInstance.getAuthGroup();
        this.authName = authInstance.getAuthDisplayName();
    }

    public UserAuthVo(AuthBase authInstance) {
        this.userUuid = UserContext.get().getUserUuid();
        this.auth = authInstance.getAuthName();
        this.authGroup = authInstance.getAuthGroup();
        this.authName = authInstance.getAuthDisplayName();
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
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

    public String getAuthName() {
        if (StringUtils.isNotBlank(auth)) {
            AuthBase authVo = AuthFactory.getAuthInstance(auth);
            if (authVo != null) {
                return authVo.getAuthDisplayName();
            }
        }
        return authName;
    }

}
