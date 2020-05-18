package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.AuthVo;
import codedriver.framework.dto.RoleAuthVo;
import codedriver.framework.dto.RoleVo;
import codedriver.framework.dto.UserVo;


public interface RoleMapper {

	public int checkRoleIsExists(String uuid);
	
	public List<RoleVo> searchRole(RoleVo roleVo);

	public List<RoleAuthVo> searchRoleAuthByRoleUuid(String roleUuid);

	public int searchRoleCount(RoleVo roleVo);

	public int searchRoleUserCountByRoleUuid(String roleUuid);

	public RoleVo getRoleByUuid(String uuid);

	public List<RoleVo> getRoleByUuidList(List<String> uuidList);

	public List<AuthVo> getRoleCountByAuth();

	public int insertRoleAuth(RoleAuthVo roleAuthVo);

	public int insertRole(RoleVo roleVo);

	public int insertRoleUser(UserVo userVo);

	public int updateRole(RoleVo roleVo);

	public int deleteRoleAuthByRoleUuid(String roleUuid);

	public int deleteRoleAuth(RoleVo roleVo);

	public int deleteRoleByUuid(String uuid);

	public int deleteMenuRoleByRoleUuid(String roleUuid);

	public int deleteUserRoleByRoleUuid(String roleUuid);

	public int deleteTeamRoleByRoleUuid(String roleUuid);

	public int deleteRoleAuthByAuth(String auth);


}
