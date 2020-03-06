package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.TeamVo;
import org.apache.ibatis.annotations.Param;

public interface TeamMapper {
	public TeamVo getTeamByUuid(String uuid);

	public List<TeamVo> getTeamByParentUuid(String parentUuid);

	public int getMaxTeamSortByParentUuid(String parentUuid);

	public List<TeamVo> searchTeam(TeamVo teamVo);

	public List<TeamVo> getTeamTree();

	public List<TeamVo> getTeamSortAfterTeamList(@Param("parentUuid") String parentUuid, @Param("sort") int sort);

	public int searchTeamCount(TeamVo teamVo);

	public int searchUserCountByTeamUuid(String uuid);

	public List<TeamVo> getTeamByUuidList(List<String> teamUuidList);

	public int deleteTeamByUuid(String uuid);

	public int deleteTeamTagByUuid(String uuid);

	public int deleteUserTeamByTeamUuid(String uuid);

	public int deleteUserTeamRoleByTeamUuid(String uuid);

	public  int insertTeam(TeamVo teamVo);

	public int insertTeamTag(TeamVo teamVo);

	public void updateTeamByUuid(TeamVo teamVo);

	public void updateTeamNameByUuid(TeamVo teamVo);

	public void updateTeamSortAndParentUuid(TeamVo teamVo);

	public void updateTeamSortAdd(String teamUuid);
}
