package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.*;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

	public int checkUserIsInTeam(@Param("userUuid") String userUuid, @Param("teamUuid") String teamUuid);
	
	public UserVo getUserBaseInfoByUuid(String userUuid);

	public UserVo getUserByUuid(String uuid);

	public List<String> getLeaderUserUuidByTeamIds(@Param("teamUuidIdList") List<String> teamUuidIdList);

	public List<UserVo> searchUser(UserVo userVo);

	public List<UserVo> searchUserByAuth(String auth);

	public List<UserVo> searchRoleUserByAuth(String auth);

	public List<UserAuthVo> searchUserAuthByUserUuid(String userUuid);
	
	public List<UserAuthVo> searchUserAllAuthByUserAuth(UserAuthVo userAuthVo);

	public List<RoleAuthVo> searchUserRoleAuthByUserUuid(String userUuid);

	public List<Long> getLimitUserPasswordIdList(String userUuid);

	public int searchUserCount(UserVo userVo);

	public List<UserVo> getActiveUserByTeamId(String teamId);

	public List<UserVo> getActiveUserByRoleName(String roleName);

	public UserVo getUserByUserIdAndPassword(UserVo userVo);

	public UserSessionVo getUserSessionByUserUuid(String userUuid);

	public List<UserVo> getUserByUserUuidList(List<String> userUuidList);

	public List<AuthVo> getUserCountByAuth();
	
	public List<UserProfileVo> getUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId);

	public int insertUserAuth(UserAuthVo userAuthVo);

	public int insertUser(UserVo userVo);

	public int insertUserPassword(UserVo userVo);

	public int updateUserPasswordActive(String userUuid);

	public int insertUserRole(@Param("userUuid") String userUuid, @Param("roleName") String roleName);

	public int insertUserTeam(@Param("userUuid") String userUuid, @Param("teamUuid") String teamUuid);
	
	public int insertUserProfile(UserProfileVo userProfileVo);

	public int insertUserSession(String userUuid);

	public int updateUser(UserVo userVo);

	public int updateUserActive(UserVo userVo);

	public int updateUserSession(String userUuid);
	
	public int updateUserProfileByUserUuidAndModuleId(@Param("userUuid")String userUuid, @Param("moduleId")String moduleId, @Param("config")String config);

	public int deleteUserPasswordByLimit(@Param("userUuid") String userUuid,@Param("idList") List<Long> idList);

	public int deleteUserByUuid(String uuid);

	public int deleteUserAuthByUserUuid(String userUuid);

	public int deleteUserAuth(UserVo userVo);

	public int deleteUserRoleByUserUuid(String userUuid);

	public int deleteUserSessionByUserUuid(String userUuid);

	public int deleteUserTeamByUserUuid(String userUuid);

	public int deleteUserAuthByAuth(String auth);
	
	public int deleteUserProfileByUserUuidAndModuleId(@Param("userUuid")String userUuid, @Param("moduleId")String moduleId);
}
