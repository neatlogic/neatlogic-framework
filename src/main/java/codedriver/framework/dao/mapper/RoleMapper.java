package codedriver.framework.dao.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import codedriver.framework.dto.RoleVo;

public interface RoleMapper {
	public List<RoleVo> selectAllRole();
	
	public List<RoleVo> getRoleByName(RoleVo roleVo);
		
	public RoleVo getRoleInfoByName(@Param("name") String name);
		
	public int insertRole(RoleVo roleVo);
		
	public int insertTeamChildrenRole(@Param("parentId") Long parentId, @Param("roleName") String roleName);
	
	public int updateRole(RoleVo roleVo);
	
	public int checkRoleNameExist(RoleVo roleVo);
	
	public int deleteRole(@Param("name") String name);
	
	public int deleteTeamRoleByRoleName(@Param("name") String name);
}
