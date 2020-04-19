package codedriver.framework.dto;

import org.apache.commons.lang.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.auth.core.AuthFactory;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class UserAuthVo extends BasePageVo {
    @EntityField(name = "用户ID", type = ApiParamType.STRING)
    private String userId;
    @EntityField(name = "权限", type = ApiParamType.STRING)
    private String auth;
    @EntityField(name = "权限组", type = ApiParamType.STRING)
    private String authGroup;
    @EntityField(name = "权限名", type = ApiParamType.STRING)
    private String authName;

    
    public UserAuthVo() {
		
	}
    
    public UserAuthVo(String _userId) {
		this.userId = _userId;
   	}
    
    public UserAuthVo(String _userId,String _auth) {
		this.userId = _userId;
		this.auth = _auth;
   	}

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
