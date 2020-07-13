package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.TeamUserVo;
import codedriver.framework.dto.TeamVo;
import org.apache.ibatis.annotations.Param;

public interface TeamMapper {
	public TeamVo getTeam(TeamVo teamVo);

	public int checkTeamIsExists(String uuid);
	
	public TeamVo getTeamByUuid(String uuid);

	public List<TeamVo> getTeamByParentUuid(String parentUuid);

	public List<TeamVo> searchTeam(TeamVo teamVo);

	public int searchTeamCount(TeamVo teamVo);

	public int searchUserCountByTeamUuid(String uuid);

	public List<TeamVo> getTeamByUuidList(List<String> teamUuidList);

	public List<String> getTeamUuidListByUserUuid(String userUuid);

	public String getTeamLockByUuid(String uuid);

	public TeamVo getTeamByParentUuidAndStartNum(@Param("parentUuid") String parentUuid, @Param("startNum") int startNum);

	public int checkTeamIsExistsByLeftRightCode(@Param("uuid") String uuid, @Param("lft") int lft, @Param("rht") int rht);

	public List<String> getTeamUserUuidListByLftRhtLevelTitle(
			@Param("lft") Integer lft, 
			@Param("rht") Integer rht, 
			@Param("level") String level, 
			@Param("title") String title
	);

	public List<TeamVo> getAncestorsAndSelfByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht);

	public List<TeamVo> getTeamUserCountAndChildCountListByUuidList(List<String> teamUuidList);

	public List<TeamUserVo> getTeamUserListByTeamUuid(String teamUuid);

	public TeamVo getMaxRhtCode();

	public int getTeamCountOnLock();

	public int deleteTeamTagByUuid(String uuid);

	public int deleteTeamByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht);

//	public int deleteTeamByUuid(@Param("uuid") String uuid);

	public  int insertTeam(TeamVo teamVo);

	public int insertTeamUser(TeamUserVo teamUserVo);

	public int insertTeamTag(TeamVo teamVo);

	public int updateTeamNameByUuid(TeamVo teamVo);

	public int updateTeamParentUuidByUuid(TeamVo teamVo);

	public int updateTeamLeftRightCode(@Param("uuid") String uuid, @Param("lft") int lft, @Param("rht") int rht);

	public int updateTeamUserTitle(TeamUserVo teamUserVo);

	public int batchUpdateTeamLeftCode(@Param("minCode")Integer minCode, @Param("step") int step);
	
	public int batchUpdateTeamRightCode(@Param("minCode")Integer minCode, @Param("step") int step);

	public int batchUpdateTeamLeftRightCodeByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("step") int step);

	public int deleteTeamUser(TeamUserVo teamUserVo);
}
