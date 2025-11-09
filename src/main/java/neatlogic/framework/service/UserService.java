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

package neatlogic.framework.service;

import neatlogic.framework.dto.UserVo;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<String> getUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidList(List<String> userUuidList, List<String> teamUuidList, List<String> roleUuidList);

    /**
     * @Description: 根据用户uuid集合与分组uuid集合查询激活的用户uuid
     * @Author: laiwt
     * @Date: 2021/3/5 17:06
     * @Params: [userUuidList, teamUuidList]
     * @Returns: java.util.Set<java.lang.String>
     **/
    Set<String> getUserUuidSetByUserUuidListAndTeamUuidList(List<String> userUuidList, List<String> teamUuidList);

    /**
     * 根据指定范围获取用户
     *
     * @param userVo    用户参数
     * @param rangeList 范围
     */
    void getUserByRangeList(UserVo userVo, List<String> rangeList);

    List<UserVo> getUserListByRoleUuid(String roleUuid);

    /**
     * 根据角色uuid获取其下所有用户uuid，包括分组中的用户
     *
     * @param roleUuid 角色uuid
     * @return
     */
    Set<String> getUserUuidSetByRoleUuid(String roleUuid);

    /**
     * 根据角色uuid获取其关联的所有分组uuid，如果关联的组需要穿透，则找出其所有子分组uuid
     *
     * @param roleUuid 角色uuid
     * @return
     */
    Set<String> getTeamUuidSetByRoleUuid(String roleUuid);

    /**
     * 根据用户userId获取用户token
     *
     * @param userId 用户userId
     * @return token
     */
    String getUserTokenByUser(String userId);

    /**
     * 根据用户userId获取用户token
     *
     * @param userId 用户userId
     * @return userVo
     */
    UserVo getUserByUser(String userId);

    /**
     * 更新用户会话cache
     *
     * @param userUuid 用户uuid
     */
    void updateUserCacheAndSessionByUserUuid(String userUuid);

}
