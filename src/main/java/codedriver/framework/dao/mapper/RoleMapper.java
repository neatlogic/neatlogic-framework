package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.RoleAuthVo;
import codedriver.framework.dto.RoleVo;
import codedriver.framework.dto.UserVo;
import org.apache.ibatis.annotations.Param;


public interface RoleMapper {
	public List<RoleVo> searchRole(RoleVo roleVo);

	public List<RoleAuthVo> searchRoleAuthByRoleName(String name);

	public int searchRoleCount(RoleVo roleVO);

	public int searchRoleUserCountByRoleName(String roleName);

	public RoleVo getRoleByRoleName(String name);

	public List<RoleVo> getRoleByRoleNameList(List<String> roleNameList);

	public int insertRoleAuth(RoleAuthVo roleAuthVo);

	public int insertRole(RoleVo roleVo);

	public int insertRoleUser(UserVo userVo);

	public int updateRole(RoleVo roleVo);

	public int deleteRoleAuthByRoleName(String roleName);

	public int deleteRoleAuth(RoleVo roleVo);

	public int deleteRoleByRoleName(String name);

	public int deleteMenuRoleByRoleName(String name);

	public int deleteUserRoleByRoleName(String name);

	public int deleteTeamRoleByRoleName(String name);
}
