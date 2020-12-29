package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import org.apache.ibatis.annotations.Param;

import codedriver.framework.dto.AuthVo;
import codedriver.framework.dto.RoleAuthVo;
import codedriver.framework.dto.RoleUserVo;
import codedriver.framework.dto.RoleVo;

public interface RoleMapper {
    public List<String> getRoleUuidListByAuth(@Param("authList") List<String> authList);

    public int checkRoleIsExists(String uuid);

    public List<RoleVo> searchRole(RoleVo roleVo);

    public List<RoleVo> getRoleListByAuthName(String auth);
    
    public List<String> getRoleUuidListByUserUuidList(@Param("list")List<String> userUuidList);

    public List<ValueTextVo> searchRoleForSelect(RoleVo roleVo);

    public List<RoleAuthVo> searchRoleAuthByRoleUuid(String roleUuid);

    public int searchRoleCount(RoleVo roleVo);

    public int searchRoleUserCountByRoleUuid(String roleUuid);

    public RoleVo getRoleByUuid(String uuid);

    public List<RoleVo> getRoleByUuidList(List<String> uuidList);

    public List<AuthVo> getRoleCountByAuth();

    public List<String> getRoleUuidByName(String name);

    public List<ValueTextVo> getRoleUuidAndNameMapList(List<String> list);

    public int insertRoleAuth(RoleAuthVo roleAuthVo);

    public int insertRole(RoleVo roleVo);

    public int insertRoleUser(RoleUserVo vo);

    public int updateRole(RoleVo roleVo);

    public int deleteRoleAuthByRoleUuid(String roleUuid);

    public int deleteRoleAuth(RoleAuthVo roleAuthVo);

    public int deleteRoleByUuid(String uuid);

    public int deleteMenuRoleByRoleUuid(String roleUuid);

    public int deleteTeamRoleByRoleUuid(String roleUuid);

    public int deleteRoleAuthByAuth(String auth);

    public int deleteRoleUser(RoleUserVo roleUserVo);

}
