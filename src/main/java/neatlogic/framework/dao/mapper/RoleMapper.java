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

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dto.*;
import neatlogic.framework.matrix.dto.MatrixDataVo;
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

    List<RoleVo> getRoleListContainsDeletedByUuidList(List<String> uuidList);

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

    int searchRoleCountForMatrix(MatrixDataVo searchVo);

    List<RoleVo> searchRoleListForMatrix(MatrixDataVo searchVo);

    List<RoleVo> getRoleByNameList(List<String> needSearchValue);

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
