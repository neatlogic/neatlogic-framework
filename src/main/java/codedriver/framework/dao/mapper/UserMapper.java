package codedriver.framework.dao.mapper;

import codedriver.framework.dto.UserVisitVo;
import codedriver.framework.dto.UserVo;

public interface UserMapper {

	public UserVo getUserByUserIdAndPassword(UserVo userVo);
	
	public UserVisitVo getUserVisitByUserId(String userId);
	
	public int insertUserVisit(String userId);
	
	public int updateUserVisit(String userId);
	
	public int deleteUserVisitByUserId(String userId);
	
}
