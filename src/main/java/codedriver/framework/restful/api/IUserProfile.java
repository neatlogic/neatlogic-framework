package codedriver.framework.restful.api;

import codedriver.framework.dto.UserProfileVo;

public interface IUserProfile {

	UserProfileVo getUserProfile();

	String getModuleId();
	
}
