package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.*;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
	public UserVo getUserBaseInfoByUserId(String userId);

	public int checkUserIsInTeam(@Param("userId")
	String userId, @Param("teamUuid")
	String teamUuid);

	public UserVo getUserByUserId(String userId);

	public List<String> getLeaderUserIdByTeamIds(@Param("teamUuidIdList")
	List<String> teamUuidIdList);

	public List<UserVo> searchUser(UserVo userVo);

	public List<UserAuthVo> searchUserAuthByUserId(String userId);
	
	public List<UserAuthVo> searchUserAllAuthByUserAuth(UserAuthVo userAuthVo);

	public List<RoleAuthVo> searchUserRoleAuthByUserId(String userId);

	public List<Long> getLimitUserPasswordIdList(String userId);

	public int searchUserCount(UserVo userVo);

	public List<UserVo> getActiveUserByTeamId(String teamId);

	public UserVo getUserByUserIdAndPassword(UserVo userVo);

	public UserSessionVo getUserSessionByUserId(String userId);

	public List<UserVo> getUserByUserIdList(List<String> userIdList);

	public List<AuthVo> getUserCountByAuth();

	public int insertUserAuth(UserAuthVo userAuthVo);

	public int insertUser(UserVo userVo);

	public int insertUserPassword(UserVo userVo);

	public int updateUserPasswordActive(String userId);

	public int insertUserRole(@Param("userId")
	String userId, @Param("roleName")
	String roleName);

	public int insertUserTeam(@Param("userId")
	String userId, @Param("teamUuid")
	String teamUuid);

	public int insertUserSession(String userId);

	public int updateUser(UserVo userVo);

	public int updateUserActive(UserVo userVo);

	public int updateUserSession(String userId);

	public int deleteUserPasswordByLimit(@Param("userId") String userId,@Param("idList") List<Long> idList);

	public int deleteUserByUserId(String userId);

	public int deleteUserAuthByUserId(String userId);

	public int deleteUserAuth(UserVo userVo);

	public int deleteUserRoleByUserId(String userId);

	public int deleteUserSessionByUserId(String userId);

	public int deleteUserTeamByUserId(String userId);
}
