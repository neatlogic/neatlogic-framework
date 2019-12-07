package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.TeamVo;

public interface TeamMapper {
	public TeamVo getTeamByUuid(String uuid);

	public List<TeamVo> searchTeam(TeamVo teamVo);

	public int searchTeamCount(TeamVo teamVo);

	public int deleteTeamByUuid(String uuid);

	public int deleteUserTeamByTeamUuid(String uuid);

	public int deleteUserTeamRoleByTeamUuid(String uuid);
}
