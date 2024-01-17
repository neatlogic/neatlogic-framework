/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.UserSessionVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserSessionMapper {
    UserSessionVo getUserSessionByTokenHash(String userUuid);

    List<UserSessionVo> getUserSessionByUuid(String userUuid);

    int getAllOnlineUserCount(Date sessionTime);

    List<String> getAllOnlineUser(@Param("sessionTime") Date sessionTime, @Param("startNum") Integer startNum, @Param("pageSize") Integer pageSize);

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

    int getUserSessionCountByDate(String limitDate);

    int insertUserSession(@Param("userUuid") String userUuid, @Param("tokenHash") String tokenHash, @Param("tokenCreateTime") Long tokenCreateTime, @Param("authInfo") String authInfo);

    int insertUserSessionWithoutTokenCreateTime(@Param("userUuid") String userUuid, @Param("tokenHash") String tokenHash, @Param("tokenCreateTime") Long tokenCreateTime, @Param("authInfo") String authInfo);

    int updateUserSession(String tokenHash);

    int deleteUserSessionByTokenHash(String tokenHash);

    void deleteUserSessionByUserUuid(String userUuid);

    int deleteUserSessionByExpireTime(Long expireTime);

}