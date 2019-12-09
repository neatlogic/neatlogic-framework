package codedriver.framework.dao.mapper;

import codedriver.framework.dto.UserSessionVo;
import codedriver.framework.dto.UserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

	public UserVo getUserByUserIdAndPassword(UserVo userVo);

	public List<UserVo> getActiveUserByTeamId(String teamId);

	public UserVo getActiveUserVoByUserId(String userId);
	
	public UserSessionVo getUserSessionByUserId(String userId);
	
	public int insertUserSession(String userId);
	
	public int updateUserSession(String userId);
	
	public int deleteUserSessionByUserId(String userId);
	
}
