package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.TeamVo;

public interface TeamMapper {
	public TeamVo getTeamByUuid(String uuid);

	public int getMaxTeamSortByParentUuid(String parentUuid);

	public List<TeamVo> searchTeam(TeamVo teamVo);

	public List<TeamVo> getTeamTree();

	public int searchTeamCount(TeamVo teamVo);

	public int searchUserCountByTeamUuid(String uuid);

	public List<TeamVo> getTeamByUuidList(List<String> teamUuidList);

	public int deleteTeamByUuid(String uuid);

	public int deleteUserTeamByTeamUuid(String uuid);

	public int deleteUserTeamRoleByTeamUuid(String uuid);

	public  int insertTeam(TeamVo teamVo);

	public void updateTeamByUuid(TeamVo teamVo);

	public void updateTeamNameByUuid(TeamVo teamVo);
}
