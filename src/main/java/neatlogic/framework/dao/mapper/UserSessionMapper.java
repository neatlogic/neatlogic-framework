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