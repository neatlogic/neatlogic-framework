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

    int searchRoleCount(RoleVo roleVo);

    int searchRoleUserCountByRoleUuid(String roleUuid);

    RoleVo getRoleByUuid(String uuid);

    RoleVo getRoleSimpleInfoByUuid(String uuid);

    List<RoleVo> getRoleByUuidList(List<String> uuidList);

    List<AuthVo> getRoleCountByAuth();

    List<String> getRoleUuidByName(String name);

    List<ValueTextVo> getRoleUuidAndNameMapList(List<String> list);

    List<String> getTeamUuidListByRoleUuid(String roleUuid);

    List<String> getRoleUuidListByTeamUuidList(List<String> teamUuidList);

    int insertRoleAuth(RoleAuthVo roleAuthVo);

    int insertRole(RoleVo roleVo);

    int insertRoleUser(RoleUserVo vo);

    int insertRoleTeamList(List<RoleTeamVo> roleTeamList);

    int updateRole(RoleVo roleVo);

    int deleteRoleAuthByRoleUuid(String roleUuid);

    int deleteRoleAuth(RoleAuthVo roleAuthVo);

    int deleteRoleByUuid(String uuid);

    int deleteMenuRoleByRoleUuid(String roleUuid);

    int deleteTeamRoleByRoleUuid(String roleUuid);

    int deleteRoleAuthByAuth(String auth);

    int deleteRoleUser(RoleUserVo roleUserVo);
}
