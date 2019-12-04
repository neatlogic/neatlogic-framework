package codedriver.framework.dao.mapper;

import codedriver.framework.dto.UserSessionVo;
import codedriver.framework.dto.UserVo;

public interface UserMapper {

	public UserVo getUserByUserIdAndPassword(UserVo userVo);
	
	public UserSessionVo getUserSessionByUserId(String userId);
	
	public int insertUserSession(String userId);
	
	public int updateUserSession(String userId);
	
	public int deleteUserSessionByUserId(String userId);
	
}
