package codedriver.framework.service;

import codedriver.framework.dto.UserVo;

public interface UserService {
	public UserVo getUserByUserIdAndPassword(UserVo userVo);
	
	public int saveUserExpiration(String userId);
	
	public int deleteUserExpirationByUserId(String userId);
}
