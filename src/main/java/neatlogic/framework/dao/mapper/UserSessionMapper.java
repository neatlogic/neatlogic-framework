/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.UserSessionVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserSessionMapper {
    UserSessionVo getUserSessionByUserUuid(String userUuid);

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

    int insertUserSession(String userUuid);

    int updateUserSession(String userUuid);

    int deleteUserSessionByUserUuid(String userUuid);
}