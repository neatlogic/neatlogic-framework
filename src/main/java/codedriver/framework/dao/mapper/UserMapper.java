package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dto.*;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

	public int checkUserAuthorityIsExists(@Param("userUuid") String userUuid, @Param("authList") List<String> authList);

	public int checkUserIsExists(String uuid);

	public int checkUserIsInTeam(@Param("userUuid") String userUuid, @Param("teamUuid") String teamUuid);

	public UserVo getUserBaseInfoByUuid(String userUuid);

	public UserVo getUserByUserId(String userId);

	public UserVo getUserByUuid(String uuid);

	public List<UserVo> searchUser(UserVo userVo);
	
	public List<ValueTextVo> searchUserForSelect(UserVo userVo);

	public List<UserVo> searchUserByAuth(String auth);

	public List<UserVo> searchRoleUserByAuth(String auth);

	public List<UserAuthVo> searchUserAuthByUserUuid(String userUuid);

	public List<UserAuthVo> searchUserAllAuthByUserAuth(UserAuthVo userAuthVo);

	public List<RoleAuthVo> searchUserRoleAuthByUserUuid(String userUuid);

	public List<Long> getLimitUserPasswordIdList(String userUuid);

	public int searchUserCount(UserVo userVo);

	public List<UserVo> getActiveUserByTeamId(String teamId);

	public List<UserVo> getActiveUserByRoleUuid(String roleUuid);

	public UserVo getUserByUserIdAndPassword(UserVo userVo);

	public UserSessionVo getUserSessionByUserUuid(String userUuid);

	public List<UserVo> getUserByUserUuidList(List<String> userUuidList);

	public List<AuthVo> getUserCountByAuth();

	public List<UserProfileVo> getUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId);

	public List<String> getUserUuidListByTeamUuidList(List<String> teamUuidList);

	public List<String> getUserUuidListByRoleUuidList(List<String> roleUuidList);

	public List<String> getUserNameListByUuidList(List<String> uuidList);

	public List<UserVo> getUserListByUserUuidList(List<String> userUuidList);

	public List<String> checkUserUuidListIsExists(List<String> userUuidList);

	public UserDataVo getUserDataByUserUuidAndType(@Param("userUuid") String userUuid, @Param("type") String type);

	public List<String> getRoleUuidListByUserUuid(String userUuid);

	public List<String> getTeamUuidListByUserUuid(String userUuid);

	public int checkUserExistsInUserAgent(String userUuid);

	public int checkAgentExistsInUserAgent(String agentUuid);

	/** 检查是否存在循环代理，即A已经是B的代理人，则不允许A设置代理人为B */
	public int checkExistsAgentLoop(@Param("agentUuid") String agentUuid,@Param("userUuid") String userUuid);

	public UserVo getUserAgent(String userUuid);
	
	public List<UserVo> getUserVip();

    public String getUserUuidByAgentUuidAndFunc(@Param("agentUuid") String agentUuid, @Param("func") String func);

	public List<String> getUserUuidListByTeamUuid(String teamUuid);

	public List<String> getUserUuidListByRoleUuid(String teamUuid);

	public int insertUserAuth(UserAuthVo userAuthVo);

	public int insertUser(UserVo userVo);

	public int insertUserPassword(UserVo userVo);

	public int updateUserPasswordActive(String userUuid);

	public int insertUserRole(@Param("userUuid") String userUuid, @Param("roleUuid") String roleUuid);

	public int insertUserTeam(@Param("userUuid") String userUuid, @Param("teamUuid") String teamUuid);

	public int insertUserProfile(UserProfileVo userProfileVo);

	public int insertUserSession(String userUuid);

	public int insertUserData(UserDataVo userDataVo);

	public int insertUserAgent(UserAgentVo userAgentVo);

	public int updateUser(UserVo userVo);

	public int updateUserActive(UserVo userVo);

	public int updateUserSession(String userUuid);

	public int updateUserData(UserDataVo userDataVo);

	public int updateUserInfo(UserVo userVo);

	public int updateUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId, @Param("config") String config);

	public int deleteUserPasswordByLimit(@Param("userUuid") String userUuid, @Param("idList") List<Long> idList);

	public int deleteUserByUuid(String uuid);

	public int deleteUserRoleByUserUuid(String userUuid);

	public int deleteUserSessionByUserUuid(String userUuid);

	public int deleteUserTeamByUserUuid(String userUuid);

	public int deleteUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId);

	public int deleteUserAuth(UserAuthVo userAuthVo);

	public int deleteUserAgent(String userUuid);
}
