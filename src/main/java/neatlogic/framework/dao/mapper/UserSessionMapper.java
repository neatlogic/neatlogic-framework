/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.UserSessionVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserSessionMapper {
    UserSessionVo getUserSessionByTokenHash(String userUuid);

    UserSessionVo getUserSessionByTokenHashWithoutCache(String userUuid);

    List<UserSessionVo> getUserSessionByUuid(String userUuid);

    List<UserSessionVo> getUserSessionByUuidList(List<String> userUuidList);

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

    List<String> getOnlineUserUuidListByUserUuidListAndGreaterThanSessionTime(
            @Param("userUuidList") List<String> userUuidList,
            @Param("sessionTime") Date sessionTime
    );

    int getUserSessionCountByDate(String limitDate);

    int insertUserSession(@Param("userUuid") String userUuid, @Param("tokenHash") String tokenHash, @Param("tokenCreateTime") Long tokenCreateTime, @Param("authInfoHash") String authInfoHash);

    int insertUserSessionWithoutTokenCreateTime(@Param("userUuid") String userUuid, @Param("tokenHash") String tokenHash, @Param("tokenCreateTime") Long tokenCreateTime, @Param("authInfoHash") String authInfoHash);

    int updateUserSessionAuthInfoHashByTokenHashList(@Param("tokenHashList") List<String> userSessionVoList, @Param("authInfoHash") String authInfoHash);

    int updateUserSessionAuthInfoHashByTokenHash(@Param("tokenHash")String tokenHash, @Param("authInfoHash") String authInfoHash);

    int updateUserSession(String tokenHash);

    int updateUserSessionCreateTime(@Param("tokenCreateTime")Long tokenCreateTime, @Param("tokenHash")String tokenHash);

    int deleteUserSessionByTokenHash(String tokenHash);

    int deleteUserSessionByUserUuid(String userUuid);

    int deleteUserSessionByUserUuidList(List<String> userUuid);

    int deleteUserSessionByTokenHashList(List<String> tokenHashList);

    int deleteUserSessionByExpireTime(Long expireTime);

}
