package codedriver.framework.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.dto.UserSessionVo;
import codedriver.framework.dto.UserVo;

public interface UserMapper {
	public UserVo getUserByUserId(String userId);

	public List<String> getLeaderUserIdByTeamIds(@Param("teamUuidIdList") List<String> teamUuidIdList);

	public List<UserVo> searchUser(UserVo userVo);
	
	public int searchUserCount(UserVo userVo);

	public List<UserVo> getActiveUserByTeamId(String teamId);

	public UserVo getUserByUserIdAndPassword(UserVo userVo);

	public UserSessionVo getUserSessionByUserId(String userId);

	public int insertUser(UserVo userVo);

	public int insertUserRole(@Param("userId") String userId, @Param("roleName") String roleName);

	public int insertUserTeam(@Param("userId") String userId, @Param("teamUuid") String teamUuid);

	public int insertUserSession(String userId);

	public int updateUser(UserVo userVo);

	public int updateUserSession(String userId);
	
	public int updateUserPassword(UserVo userVo);

	public int deleteUserByUserId(String userId);

	public int deleteUserRoleByUserId(String userId);

	public int deleteUserSessionByUserId(String userId);

	public int deleteUserTeamByUserId(String userId);
}
