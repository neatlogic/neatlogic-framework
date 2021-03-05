package codedriver.framework.dao.mapper;

import codedriver.framework.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserMapper {

    public int checkUserAuthorityIsExists(@Param("userUuid") String userUuid, @Param("authList") List<String> authList);

    public int checkUserIsExists(String uuid);

    public int checkUserIsInTeam(@Param("userUuid") String userUuid, @Param("teamUuid") String teamUuid);

    public UserVo getUserBaseInfoByUuid(String userUuid);

    /**
     * @Description: 此sql与getUserBaseInfoByUuid一致，有些场景下，使用缓存可能会有问题
     * 比如两个相同的userVo放在JSONArray时，第二个userVo可能会替换成$ref:
     * 所以需要一个不使用缓存的版本
     * @Author: laiwt
     * @Date: 2021/1/8 17:23
     * @Params: [userUuid]
     * @Returns: codedriver.framework.dto.UserVo
     **/
    public UserVo getUserBaseInfoByUuidWithoutCache(String userUuid);

    public UserVo getUserByUserId(String userId);

    public UserVo getUserByUuid(String uuid);

    public UserVo getUserSimpleInfoByUuid(String uuid);

    public List<UserVo> searchUser(UserVo userVo);

    public List<UserVo> searchUserForSelect(UserVo userVo);

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

    UserVo getActiveUserByUserId(UserVo userVo);

    public UserSessionVo getUserSessionByUserUuid(String userUuid);

    public List<UserVo> getUserByUserUuidList(List<String> userUuidList);

    public List<AuthVo> getUserCountByAuth();

    public List<UserProfileVo> getUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId);

    public List<String> getUserUuidListByTeamUuidList(List<String> teamUuidList);

    public List<String> getUserUuidListByRoleUuidList(List<String> roleUuidList);

    public List<String> getUserUuidListByUserName(String userName);

    public List<UserVo> getUserListByUserUuidList(List<String> userUuidList);

    public List<String> checkUserUuidListIsExists(@Param("list") List<String> userUuidList,@Param("isActive") Integer isActive);

    public UserDataVo getUserDataByUserUuidAndType(@Param("userUuid") String userUuid, @Param("type") String type);

    public List<String> getRoleUuidListByUserUuid(String userUuid);

    public List<String> getTeamUuidListByUserUuid(String userUuid);

    public List<UserVo> getUserByUserIdList(List<String> list);

    public List<UserVo> getUserListByTeamUuid(String teamUuid);

    public int checkUserExistsInUserAgent(String userUuid);

    public int checkAgentExistsInUserAgent(String agentUuid);

    /**
     * 检查是否存在循环代理，即A已经是B的代理人，则不允许A设置代理人为B
     */
    public int checkExistsAgentLoop(@Param("agentUuid") String agentUuid, @Param("userUuid") String userUuid);

    public UserVo getUserAgent(String userUuid);

    public List<UserVo> getUserVip();

    public String getUserUuidByAgentUuidAndFunc(@Param("agentUuid") String agentUuid, @Param("func") String func);

    public List<String> getUserUuidListByTeamUuid(String teamUuid);

    public List<String> getUserUuidListByRoleUuid(String teamUuid);

    public int checkUserIdIsIsRepeat(UserVo userVo);

    public List<UserVo> getUserListByUuidList(List<String> uuidList);

    public List<UserVo> getUserListByRoleUuid(String roleUuid);

    public int getOnlineUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidListAndGreaterThanSessionTimeCount(
            @Param("userUuidList") List<String> userUuidList,
            @Param("teamUuidList") List<String> teamUuidList,
            @Param("roleUuidList") List<String> roleUuidList,
            @Param("sessionTime") Date sessionTime
    );

    public List<String> getOnlineUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidListAndGreaterThanSessionTime(
            @Param("userUuidList") List<String> userUuidList,
            @Param("teamUuidList") List<String> teamUuidList,
            @Param("roleUuidList") List<String> roleUuidList,
            @Param("sessionTime") Date sessionTime,
            @Param("needPage") Boolean needPage,
            @Param("startNum") Integer startNum,
            @Param("pageSize") Integer pageSize
    );

    public int getAllOnlineUserCount(Date sessionTime);

    public List<String> getAllOnlineUser(@Param("sessionTime") Date sessionTime,@Param("startNum") Integer startNum,@Param("pageSize") Integer pageSize);

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
