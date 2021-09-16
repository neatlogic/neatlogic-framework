package codedriver.framework.dao.mapper;

import codedriver.framework.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserMapper {

    int checkUserAuthorityIsExists(@Param("userUuid") String userUuid, @Param("authList") List<String> authList);

    int checkUserIsExists(String uuid);

    int checkUserIsInTeam(@Param("userUuid") String userUuid, @Param("teamUuid") String teamUuid);

    UserVo getUserBaseInfoByUuid(String userUuid);

    /**
     * @Description: 此sql与getUserBaseInfoByUuid一致，有些场景下，使用缓存可能会有问题
     * 比如两个相同的userVo放在JSONArray时，第二个userVo可能会替换成$ref:
     * 所以需要一个不使用缓存的版本
     * @Author: laiwt
     * @Date: 2021/1/8 17:23
     * @Params: [userUuid]
     * @Returns: codedriver.framework.dto.UserVo
     **/
    UserVo getUserBaseInfoByUuidWithoutCache(String userUuid);

    UserVo getUserByUserId(String userId);

    UserVo getUserByUuid(String uuid);

    UserVo getUserSimpleInfoByUuid(String uuid);

    List<UserVo> getUserByMail(String value);

    List<UserVo> searchUser(UserVo userVo);

    List<UserVo> searchUserForSelect(UserVo userVo);

    List<UserVo> searchUserByAuth(String auth);

    List<UserAuthVo> searchUserAuthByUserUuid(String userUuid);

    List<UserAuthVo> searchUserAllAuthByUserAuth(UserAuthVo userAuthVo);

    List<UserAuthVo> searchUserAllAuthByUserAuthCache(UserAuthVo userAuthVo);

    List<RoleAuthVo> searchUserRoleAuthByUserUuid(String userUuid);

    List<Long> getLimitUserPasswordIdList(String userUuid);

    int searchUserCount(UserVo userVo);

    List<UserVo> getActiveUserByTeamId(String teamId);

    List<UserVo> getActiveUserByRoleUuid(String roleUuid);

    UserVo getUserByUserIdAndPassword(UserVo userVo);

    UserVo getActiveUserByUserId(UserVo userVo);

    UserSessionVo getUserSessionLockByUserUuid(String userUuid);

    List<UserVo> getUserByUserUuidList(List<String> userUuidList);

    List<AuthVo> getUserCountByAuth();

    List<UserProfileVo> getUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId);

    List<String> getUserUuidListByTeamUuidList(List<String> teamUuidList);

    List<String> getUserUuidListByTeamUuidListLimitTwo(List<String> teamUuidList);

    /**
     * @Description: 根据分组uuid，找出所有用户uuid与其所在所有分组的uuid和角色uuid
     * @Author: laiwt
     * @Date: 2021/3/12 15:15
     * @Params: [teamUuidList]
     * @Returns: java.util.List<codedriver.framework.dto.UserVo>
     **/
    List<UserVo> getUserTeamRoleListByTeamUuidList(List<String> teamUuidList);

    List<String> getUserUuidListByRoleUuidList(List<String> roleUuidList);

    List<String> getUserUuidListByUserName(String userName);

    List<UserVo> getUserListByUserUuidList(@Param("list") List<String> userUuidList, @Param("isActive") Integer isActive);

    List<String> checkUserUuidListIsExists(@Param("list") List<String> userUuidList, @Param("isActive") Integer isActive);

    UserDataVo getUserDataByUserUuidAndType(@Param("userUuid") String userUuid, @Param("type") String type);

//    List<String> getRoleUuidListByUserUuid(String userUuid);

//    List<String> getTeamUuidListByUserUuid(String userUuid);

    List<UserVo> getUserByUserIdList(List<String> list);

    List<UserVo> getUserListByTeamUuid(String teamUuid);

    UserTitleVo getUserTitleLockByName(String title);

    UserTitleVo getUserTitleByName(String title);

    int checkUserExistsInUserAgent(String userUuid);

    int checkAgentExistsInUserAgent(String agentUuid);

    /**
     * 检查是否存在循环代理，即A已经是B的代理人，则不允许A设置代理人为B
     */
    int checkExistsAgentLoop(@Param("agentUuid") String agentUuid, @Param("userUuid") String userUuid);

    UserVo getUserAgent(String userUuid);

    List<UserVo> getUserVip();

    String getUserUuidByAgentUuidAndFunc(@Param("agentUuid") String agentUuid, @Param("func") String func);

    List<String> getUserUuidListByTeamUuid(String teamUuid);

    List<String> getUserUuidListByRoleUuid(String teamUuid);

    int checkUserIdIsIsRepeat(UserVo userVo);

    List<UserVo> getUserListByUuidList(List<String> uuidList);

    List<UserVo> getUserListByRoleUuid(UserVo userVo);

    int getOnlineUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidListAndGreaterThanSessionTimeCount(
            @Param("userUuidList") List<String> userUuidList,
            @Param("teamUuidList") List<String> teamUuidList,
            @Param("roleUuidList") List<String> roleUuidList,
            @Param("sessionTime") Date sessionTime
    );

    List<String> getOnlineUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidListAndGreaterThanSessionTime(
            @Param("userUuidList") List<String> userUuidList,
            @Param("teamUuidList") List<String> teamUuidList,
            @Param("roleUuidList") List<String> roleUuidList,
            @Param("sessionTime") Date sessionTime,
            @Param("needPage") Boolean needPage,
            @Param("startNum") Integer startNum,
            @Param("pageSize") Integer pageSize
    );

    int getAllOnlineUserCount(Date sessionTime);

    List<String> getAllOnlineUser(@Param("sessionTime") Date sessionTime, @Param("startNum") Integer startNum, @Param("pageSize") Integer pageSize);

    List<UserTitleVo> getUserTitleListLockByTitleNameList(@Param("titleNameList") List<String> titleList);

    int searchUserTitleCount(UserTitleVo userTitleVo);

    List<UserTitleVo> searchUserTitle(UserTitleVo userTitleVo);

    UserTitleVo getUserTitleById(Long id);

    List<UserTitleVo> getUserTitleListByUserUuid(String userUuid);

    List<String> checkUserInRangeList(@Param("userUuidList") List<String> userUuidList, @Param("user") UserVo userVo);

    int insertUserAuth(UserAuthVo userAuthVo);

    int insertUser(UserVo userVo);

    int insertUserPassword(UserVo userVo);

    int updateUserPasswordActive(String userUuid);

    int insertUserRole(@Param("userUuid") String userUuid, @Param("roleUuid") String roleUuid);

    int insertUserTeam(@Param("userUuid") String userUuid, @Param("teamUuid") String teamUuid);

    int insertUserProfile(UserProfileVo userProfileVo);

    int insertUserSession(String userUuid);

    int insertUserData(UserDataVo userDataVo);

    int insertUserAgent(UserAgentVo userAgentVo);

    int insertUserTitle(UserTitleVo userTitleVo);

    int updateUser(UserVo userVo);

    int updateUserActive(UserVo userVo);

    int updateUserSession(String userUuid);

    int updateUserData(UserDataVo userDataVo);

    int updateUserInfo(UserVo userVo);

    int updateUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId, @Param("config") String config);

    int deleteUserPasswordByLimit(@Param("userUuid") String userUuid, @Param("idList") List<Long> idList);

    int deleteUserByUuid(String uuid);

    int deleteUserRoleByUserUuid(String userUuid);

    int deleteUserSessionByUserUuid(String userUuid);

    int deleteUserTeamByUserUuid(String userUuid);

    int deleteUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId);

    int deleteUserAuth(UserAuthVo userAuthVo);

    int deleteUserAgent(String userUuid);

    int deleteUserTitleByName(String name);

    int searchUserCountByAuth(UserVo vo);

    List<String> searchUserUuIdByUser(UserVo vo);
}
