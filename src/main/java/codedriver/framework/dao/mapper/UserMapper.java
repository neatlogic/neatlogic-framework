package codedriver.framework.dao.mapper;

import codedriver.framework.dto.UserExpirationVo;
import codedriver.framework.dto.UserVo;

public interface UserMapper {

	public UserVo getUserByUserIdAndPassword(UserVo userVo);
	
	public UserExpirationVo getUserExpirationByUserId(String userId);
	
	public int replaceUserExpiration(UserExpirationVo userExpirationVo);
	
	public int deleteUserExpirationByUserId(String userId);
	
	

}
