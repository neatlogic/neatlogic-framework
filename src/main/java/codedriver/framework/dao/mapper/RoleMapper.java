/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.mapper;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    List<String> getRoleUuidListByAuth(@Param("authList") List<String> authList);

    int checkRoleIsExists(String uuid);

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

    RoleVo getRoleSimpleInfoByUuid(String uuid);

    List<RoleVo> getRoleByUuidList(List<String> uuidList);

    List<AuthVo> getRoleCountByAuth();

    List<String> getRoleUuidByName(String name);

    List<ValueTextVo> getRoleUuidAndNameMapList(List<String> list);

    List<RoleTeamVo> getRoleTeamListByRoleUuid(String roleUuid);

    List<RoleTeamVo> getRoleTeamListByRoleUuidAndTeamUuidList(@Param("roleUuid") String roleUuid, @Param("teamUuidList") List<String> teamUuidList);

    List<RoleTeamVo> getRoleTeamListByRoleUuidList(@Param("list") List<String> roleList);

    List<RoleUserVo> getRoleUserListByRoleUuidList(@Param("list")List<String> roleList);

    List<String> getRoleUuidListByTeamUuidListAndCheckedChildren(@Param("teamUuidList") List<String> teamUuidList, @Param("checkedChildren") Integer checkedChildren);

    /**
     * 根据team的uuid获取当前组的roleList
     *
     * @param uuidList
     * @return
     */
    List<RoleVo> getRoleListByTeamUuidList(List<String> uuidList);

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

    int deleteRoleAuthByRoleUuid(String roleUuid);

    int deleteRoleAuth(RoleAuthVo roleAuthVo);

    int deleteRoleByUuid(String uuid);

    int deleteMenuRoleByRoleUuid(String roleUuid);

    int deleteTeamRoleByRoleUuid(String roleUuid);

    int deleteRoleAuthByAuth(String auth);

    int deleteRoleUser(RoleUserVo roleUserVo);

    int deleteTeamRoleByRoleUuidAndTeamUuidList(@Param("roleUuid") String roleUuid, @Param("teamUuidList") List<String> teamUuidList);

    int deleteTeamRole(RoleTeamVo roleTeamVo);

}
