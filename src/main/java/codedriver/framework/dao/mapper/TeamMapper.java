package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.TeamVo;
import org.apache.ibatis.annotations.Param;

public interface TeamMapper {
	public TeamVo getTeam(TeamVo teamVo);

	public int checkTeamIsExists(String uuid);
	
	public TeamVo getTeamByUuid(String uuid);

	public List<TeamVo> getTeamByParentUuid(String parentUuid);

	public int getMaxTeamSortByParentUuid(String parentUuid);

	public List<TeamVo> searchTeam(TeamVo teamVo);

//	public List<TeamVo> getTeamTree();

//	public List<TeamVo> getTeamSortAfterTeamList(@Param("parentUuid") String parentUuid, @Param("sort") int sort);
	
//	public List<TeamVo> getTeamSortUpTeamList(@Param("parentUuid") String parentUuid, @Param("sort") int sort, @Param("targetSort") int targetSort);
	
//	public List<TeamVo> getTeamSortDownTeamList(@Param("parentUuid") String parentUuid, @Param("sort") int sort, @Param("targetSort") int targetSort);

	public int searchTeamCount(TeamVo teamVo);

	public int searchUserCountByTeamUuid(String uuid);

	public List<TeamVo> getTeamByUuidList(List<String> teamUuidList);

	public List<String> getTeamUuidListByUserUuid(String userUuid);

	public String getTeamLockByUuid(String uuid);

	public TeamVo getTeamByParentUuidAndSort(@Param("parentUuid") String parentUuid, @Param("sort") int sort);

	public int checkTeamIsExistsByLeftRightCode(@Param("uuid") String uuid, @Param("lft") int lft, @Param("rht") int rht);

	public int deleteTeamByUuid(String uuid);

	public int deleteTeamTagByUuid(String uuid);

	public int deleteUserTeamByTeamUuid(String uuid);

	public int deleteUserTeamRoleByTeamUuid(String uuid);

	public int deleteTeamByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht);

	public  int insertTeam(TeamVo teamVo);

	public int insertTeamUser(@Param("teamUuid") String teamUuid, @Param("userUuid") String userUuid);

	public int insertTeamTag(TeamVo teamVo);

//	public int updateTeamByUuid(TeamVo teamVo);

	public int updateTeamNameByUuid(TeamVo teamVo);

	public int updateTeamSortAndParentUuid(TeamVo teamVo);

//	public int updateTeamSortAdd(String teamUuid);
//
//	public int updateTeamSortDec(String teamUuid);

	public int updateTeamLeftRightCode(@Param("uuid") String uuid, @Param("lft") int lft, @Param("rht") int rht);

	public int batchUpdateTeamLeftCode(@Param("minCode")Integer minCode, @Param("step") int step);
	
	public int batchUpdateTeamRightCode(@Param("minCode")Integer minCode, @Param("step") int step);

	public int updateSortIncrement(@Param("parentUuid")String parentUuid, @Param("fromSort")Integer fromSort, @Param("toSort")Integer toSort);

	public int updateSortDecrement(@Param("parentUuid")String parentUuid, @Param("fromSort")Integer fromSort, @Param("toSort")Integer toSort);

	public int batchUpdateTeamLeftRightCodeByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("step") int step);
}
