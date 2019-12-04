package codedriver.framework.service;

import codedriver.framework.dto.UserVo;

public interface UserService {
	public UserVo getUserByUserIdAndPassword(UserVo userVo);
	
	public int saveUserVisit(String userId);
	
	public int deleteUserVisitByUserId(String userId);
}
