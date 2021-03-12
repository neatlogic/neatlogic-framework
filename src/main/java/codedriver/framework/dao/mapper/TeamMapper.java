package codedriver.framework.dao.mapper;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dto.TeamUserVo;
import codedriver.framework.dto.TeamVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeamMapper {
	public TeamVo getTeam(TeamVo teamVo);

	public TeamVo getTeamSimpleInfoByUuid(TeamVo teamVo);

	public int checkTeamIsExists(String uuid);

	public List<String> checkTeamUuidListIsExists(List<String> uuidList);

	public TeamVo getTeamByUuid(String uuid);

	public List<TeamVo> getTeamByParentUuid(String parentUuid);
	
    public List<TeamVo> getDepartmentTeamUuidByTeamList(@Param("list")List<TeamVo> teamList);
	
	public List<TeamVo> getAllSonTeamByParentTeamList(@Param("list")List<TeamVo> list);

	public List<TeamVo> searchTeam(TeamVo teamVo);
	
	public List<TeamVo> searchTeamByUserUuidAndLevelList(@Param("userUuid") String userUuid,@Param("list") List<String> level);

	public List<ValueTextVo> searchTeamForSelect(TeamVo teamVo);

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
	
	public List<String> getTeamUserUuidListByLftRhtTitle(
	    @Param("lft") Integer lft, 
	    @Param("rht") Integer rht, 
	    @Param("title") String title
	    );
	
	public List<TeamVo> getAncestorsAndSelfByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("level") String level);

	public List<TeamVo> getTeamUserCountAndChildCountListByUuidList(@Param("list") List<String> teamUuidList,@Param("isActive") Integer isActive);

	public List<TeamUserVo> getTeamUserListByTeamUuid(String teamUuid);

    public Integer getMaxRhtCode();

    public int getTeamCount();
    /**
     * 
    * @Time:2020年7月20日
    * @Description: 判断左右编码是否全部正确，符合下列条件的才正确
    * 1.左右编码不能为null
    * 2.左编码不能小于2，右编码不能小于3
    * 3.子节点的左编码大于父节点的左编码，子节点的右编码小于父节点的右编码
    * 4.没有子节点的节点左编码比右编码小1
    * @return int 返回左右编码不正确的个数
     */
    public int checkLeftRightCodeIsWrong();

    public List<String> getTeamUuidByName(String name);

    public List<ValueTextVo> getTeamUuidAndNameMapList(List<String> list);

    public List<TeamVo> getRepeatTeamNameByNameList(List<String> list);

	public int deleteTeamByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht);

	public  int insertTeam(TeamVo teamVo);

	public int insertTeamUser(TeamUserVo teamUserVo);

	public int updateTeamNameByUuid(TeamVo teamVo);

	public int updateTeamParentUuidByUuid(TeamVo teamVo);
	
	public int updateTeamParentUuidAndNameByUuid(TeamVo teamVo);

	public int updateTeamLeftRightCode(@Param("uuid") String uuid, @Param("lft") int lft, @Param("rht") int rht);

	public int updateTeamUserTitle(TeamUserVo teamUserVo);

	public int batchUpdateTeamLeftCode(@Param("minCode")Integer minCode, @Param("step") int step);
	
	public int batchUpdateTeamRightCode(@Param("minCode")Integer minCode, @Param("step") int step);

	public int batchUpdateTeamLeftRightCodeByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("step") int step);

	public int deleteTeamUser(TeamUserVo teamUserVo);

}
