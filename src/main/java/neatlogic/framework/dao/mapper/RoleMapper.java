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

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    List<String> getRoleUuidListByAuth(@Param("authList") List<String> authList);

    int checkRoleIsExists(String uuid);

    int checkRoleNameIsIsRepeat(RoleVo roleVo);

    List<RoleVo> searchRole(RoleVo roleVo);

    List<RoleVo> getRoleListByAuthName(String auth);

    List<String> getRoleUuidListByUserUuidList(@Param("list") List<String> userUuidList);

    List<String> getRoleUuidListByUserUuid(String userUuid);

    List<ValueTextVo> searchRoleForSelect(RoleVo roleVo);

    List<RoleAuthVo> searchRoleAuthByRoleUuid(String roleUuid);

    List<RoleVo> getTeamCountListByRoleUuidList(List<String> roleUuidLIst);

    List<RoleVo> getUserCountListByRoleUuidList(List<String> roleUuidLIst);

    int searchRoleCount(RoleVo roleVo);

    int searchRoleUserCountByRoleUuid(String roleUuid);

    RoleVo getRoleByUuid(String uuid);

    RoleVo getRoleByName(String name);

    RoleVo getRoleSimpleInfoByUuid(String uuid);

    List<RoleVo> getRoleByUuidList(List<String> uuidList);

    List<RoleVo> getRoleByIdList(List<Long> idList);

    List<RoleVo> getRoleRuleByUuidList(List<String> uuidList);

    List<String> getRoleUuidListByUuidList(List<String> uuidList);

    List<AuthVo> getRoleCountByAuth();

    List<String> getRoleUuidByName(String name);

    List<String> getRoleUuidByNameList(@Param("nameList") List nameList);

    List<ValueTextVo> getRoleUuidAndNameMapList(List<String> list);

    List<RoleTeamVo> getRoleTeamListByRoleUuid(String roleUuid);

    List<RoleTeamVo> getRoleTeamListByRoleUuidAndTeamUuidList(@Param("roleUuid") String roleUuid, @Param("teamUuidList") List<String> teamUuidList);

    List<RoleTeamVo> getRoleTeamListByRoleUuidList(@Param("list") List<String> roleList);

    List<RoleUserVo> getRoleUserListByRoleUuidList(@Param("list") List<String> roleList);

    List<String> getRoleUuidListByTeamUuidList(@Param("teamUuidList") List<String> teamUuidList);

    List<String> getRoleUuidListByTeamUuidListAndCheckedChildren(@Param("teamUuidList") List<String> teamUuidList, @Param("checkedChildren") Integer checkedChildren);

    /**
     * 根据team的uuid获取当前组的roleList
     *
     * @param uuidList
     * @return
     */
    List<RoleVo> getRoleListWithTeamByTeamUuidList(List<String> uuidList);

    /**
     * 根据team的左右编码查询父分组的并且可穿透的roleList
     *
     * @param teamVo
     * @return
     */
    List<RoleVo> getParentTeamRoleListWithCheckedChildrenByTeam(TeamVo teamVo);

    int insertRoleAuth(RoleAuthVo roleAuthVo);

    int insertRole(RoleVo roleVo);

    int replaceRoleUser(RoleUserVo vo);

    int insertRoleTeam(RoleTeamVo roleTeamVo);

    int insertRoleTeamList(List<RoleTeamVo> roleTeamList);

    int updateRole(RoleVo roleVo);

    int updateTeamRole(RoleTeamVo roleTeamVo);

    int updateRoleIsDeletedByUuid(String uuid);

    int deleteRoleAuthByRoleUuid(String roleUuid);

    int deleteRoleAuth(RoleAuthVo roleAuthVo);

//    int deleteRoleByUuid(String uuid);

    int deleteMenuRoleByRoleUuid(String roleUuid);

    int deleteTeamRoleByRoleUuid(String roleUuid);

    int deleteRoleAuthByAuth(String auth);

    int deleteRoleUser(RoleUserVo roleUserVo);

    int deleteTeamRoleByRoleUuidAndTeamUuidList(@Param("roleUuid") String roleUuid, @Param("teamUuidList") List<String> teamUuidList);

    int deleteTeamRole(RoleTeamVo roleTeamVo);

}
