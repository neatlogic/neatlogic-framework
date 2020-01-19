package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.RoleVo;

public interface RoleMapper {
	public List<RoleVo> searchRole(RoleVo roleVo);

	public int searchRoleCount(RoleVo roleVO);

	public RoleVo getRoleByRoleName(String name);

	public List<RoleVo> getRoleByRoleNameList(List<String> roleNameList);

	public int insertRole(RoleVo roleVo);

	public int updateRole(RoleVo roleVo);

	public int deleteRoleByRoleName(String name);

	public int deleteMenuRoleByRoleName(String name);

	public int deleteUserRoleByRoleName(String name);

	public int deleteTeamRoleByRoleName(String name);
}
