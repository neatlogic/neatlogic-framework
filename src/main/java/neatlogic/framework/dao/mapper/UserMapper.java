/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.*;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    String getUserTokenByUserId(String userId);

    String getUserTokenByUser(String userId);


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
     * @Returns: neatlogic.framework.dto.UserVo
     **/
    UserVo getUserBaseInfoByUuidWithoutCache(String userUuid);

    UserVo getUserByUserId(String userId);

    UserVo getUserByUserUuid(String userId);

    UserVo getUserByUser(String user);

    UserVo getUserById(Long id);

    UserVo getUserByUuid(@Param("uuid") String uuid);

    UserVo getUserSimpleInfoByUuid(String uuid);

    List<UserVo> getUserByMail(String value);

    List<String> searchUserUuidList(UserVo userVo);

    List<UserVo> searchUserDetailInfoByUuidList(List<String> uuidList);

    List<UserVo> searchUserForGroupSearch(UserVo userVo);

    List<UserVo> searchUserForSelect(UserVo userVo);

    List<UserVo> searchUserByAuth(String auth);

    List<UserAuthVo> searchUserAuthByUserUuid(String userUuid);

    List<UserAuthVo> searchUserAllAuthByUserAuth(@Param("authenticationInfoVo") AuthenticationInfoVo authenticationInfoVo);

    List<RoleAuthVo> searchUserRoleAuthByUserUuid(String userUuid);

    List<Long> getLimitUserPasswordIdList(String userUuid);

    int searchUserCount(UserVo userVo);

    List<UserVo> getActiveUserByTeamId(String teamId);

    List<UserVo> getActiveUserByRoleUuid(String roleUuid);

    UserVo getUserByUserIdAndPassword(UserVo userVo);

    UserVo getActiveUserByUserId(UserVo userVo);

    List<UserVo> getUserByUserUuidList(List<String> userUuidList);

    List<UserVo> getUserByIdList(List<Long> idList);

    List<AuthVo> getUserCountByAuth();

    List<UserProfileVo> getUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId);

    List<String> getUserUuidListByTeamUuidList(List<String> teamUuidList);

    List<String> getUserUuidListByTeamUuidListLimitTwo(List<String> teamUuidList);

    /**
     * @Description: 根据分组uuid，找出所有用户uuid与其所在所有分组的uuid和角色uuid
     * @Author: laiwt
     * @Date: 2021/3/12 15:15
     * @Params: [teamUuidList]
     * @Returns: java.util.List<neatlogic.framework.dto.UserVo>
     **/
    List<UserVo> getUserTeamRoleListByTeamUuidList(List<String> teamUuidList);

    List<String> getUserUuidListByRoleUuidList(List<String> roleUuidList);

    List<String> getUserUuidListByUserName(String userName);

    List<UserVo> getUserListByUserUuidList(@Param("list") List<String> userUuidList, @Param("isActive") Integer isActive);

    List<String> getUserUuidListByUuidListAndIsActive(@Param("list") List<String> userUuidList, @Param("isActive") Integer isActive);

    UserDataVo getUserDataByUserUuidAndType(@Param("userUuid") String userUuid, @Param("type") String type);

//    List<String> getRoleUuidListByUserUuid(String userUuid);

//    List<String> getTeamUuidListByUserUuid(String userUuid);

    List<UserVo> getUserByUserIdList(List<String> list);

    List<UserVo> getUserListByTeamUuid(String teamUuid);

    UserTitleVo getUserTitleLockByName(String title);

    UserTitleVo getUserTitleByName(String title);

//    int checkUserExistsInUserAgent(String userUuid);

//    int checkAgentExistsInUserAgent(String agentUuid);

    /**
     * 检查是否存在循环代理，即A已经是B的代理人，则不允许A设置代理人为B
     */
//    int checkExistsAgentLoop(@Param("agentUuid") String agentUuid, @Param("userUuid") String userUuid);

//    UserVo getUserAgent(String userUuid);

    List<UserVo> getUserVip();

//    String getUserUuidByAgentUuidAndFunc(@Param("agentUuid") String agentUuid, @Param("func") String func);

    List<String> getUserUuidListByTeamUuid(String teamUuid);

    List<String> getUserUuidListByRoleUuid(String roleUuid);

    int checkUserIdIsIsRepeat(UserVo userVo);

    List<UserVo> getUserListByUuidList(List<String> uuidList);

    List<UserVo> getUserListByRoleUuid(UserVo userVo);

    List<UserTitleVo> getUserTitleListLockByTitleNameList(@Param("titleNameList") List<String> titleList);

    int searchUserTitleCount(UserTitleVo userTitleVo);

    List<UserTitleVo> searchUserTitle(UserTitleVo userTitleVo);

    UserTitleVo getUserTitleById(Long id);

    List<UserTitleVo> getUserTitleListByUserUuid(String userUuid);

    List<String> checkUserInRangeList(@Param("userUuidList") List<String> userUuidList, @Param("user") UserVo userVo);

    /**
     * 根据用户uuid列表查询激活用户（不查询角色信息）
     *
     * @return
     */
    List<UserVo> getActiveUserListExcludeRoleInfoByUserUuidList(List<String> uuidList);

    List<UserVo> getSuperAdminList();

    int getUserTeamCountByUserUuid(String userUuid);

    List<String> getActiveUserEmailListByUserUuidList(List<String> userUuidList);

    List<String> getActiveUserEmailListByTeamUuid(String teamUuid);

    List<String> getUserIdByUserIdList(List<String> userIdList);

    int searchUserCountForMatrix(MatrixDataVo searchVo);

    List<Map<String, Object>> searchUserListForMatrix(MatrixDataVo searchVo);

    int insertUserAuth(UserAuthVo userAuthVo);

    int insertUser(UserVo userVo);

    int insertUserPassword(UserVo userVo);

    int insertUserRole(@Param("userUuid") String userUuid, @Param("roleUuid") String roleUuid);

    int insertUserTeam(@Param("userUuid") String userUuid, @Param("teamUuid") String teamUuid);

    int insertUserProfile(UserProfileVo userProfileVo);

    int insertUserData(UserDataVo userDataVo);

//    int insertUserAgent(UserAgentVo userAgentVo);

    int insertUserTitle(UserTitleVo userTitleVo);

//    int batchInsertUser(List<UserVo> list);

    int insertUserForLdap(UserVo userVo);

//    int batchInsertUserTeam(List<TeamUserVo> list);

    int batchInsertUserRole(List<RoleUserVo> list);

    int updateUserPasswordActive(String userUuid);

    int updateAllUserPasswordExcludeAdmin(String password);

    int updateUser(UserVo userVo);

    int updateUserActive(UserVo userVo);

    int updateUserData(UserDataVo userDataVo);

    int updateUserInfo(UserVo userVo);

    int updateUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId, @Param("config") String config);

    int updateUserTeamByUserUuid(TeamUserVo vo);

    int updateUserIsDeletedByUuid(String uuid);

    int updateUserIsDeletedBySourceAndLcd(@Param("source") String source, @Param("lcd") Date lcd);

    int updateUserIsNotDeletedByUuid(String uuid);

    int deleteUserPasswordByLimit(@Param("userUuid") String userUuid, @Param("idList") List<Long> idList);

    int deleteUserPasswordByUserUuid(String userUuid);

    int deleteUserByUuid(String uuid);

    int deleteUserRoleByUserUuid(String userUuid);

    int deleteUserTeamByUserUuid(String userUuid);

    int deleteUserProfileByUserUuidAndModuleId(@Param("userUuid") String userUuid, @Param("moduleId") String moduleId);

    int deleteUserAuth(UserAuthVo userAuthVo);

//    int deleteUserAgent(String userUuid);

    int deleteUserTitleByName(String name);

//    int bacthDeleteUserTeam(@Param("userUuidlist") List<String> userUuidlist, @Param("source") String source);

    int bacthDeleteUserTeamByUserUuid(@Param("userUuid") String userUuid, @Param("source") String source);

    int searchUserCountByAuth(UserVo vo);

    List<String> searchUserUuIdByUser(UserVo vo);

    void updateUserTokenByUuid(@Param("token") String token, @Param("uuid") String uuid);

    void deleteUserDataByUserUuid(String userUuid);
}
