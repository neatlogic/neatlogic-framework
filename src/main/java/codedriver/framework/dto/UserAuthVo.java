package codedriver.framework.dto;

import org.apache.commons.lang.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.auth.core.AuthFactory;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class UserAuthVo extends BasePageVo {
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
    
    public UserAuthVo(String _userUuid,String _auth) {
		this.userUuid = _userUuid;
		this.auth = _auth;
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
		if(StringUtils.isNotBlank(auth)) {
			AuthBase authVo =  AuthFactory.getAuthInstance(auth);
			if(authVo != null) {
				return authVo.getAuthDisplayName();
			}
		}
		return authName;
	}

}
