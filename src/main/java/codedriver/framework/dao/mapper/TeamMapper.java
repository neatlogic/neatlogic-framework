package codedriver.framework.dao.mapper;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dto.TeamUserTitleVo;
import codedriver.framework.dto.TeamUserVo;
import codedriver.framework.dto.TeamVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeamMapper {
    TeamVo getTeam(TeamVo teamVo);

    TeamVo getTeamSimpleInfoByUuid(TeamVo teamVo);

    int checkTeamIsExists(String uuid);

    List<String> checkTeamUuidListIsExists(List<String> uuidList);

    TeamVo getTeamByUuid(String uuid);

    List<TeamVo> getTeamByParentUuid(String parentUuid);

    List<TeamVo> getDepartmentTeamUuidByTeamList(@Param("list") List<TeamVo> teamList);

    List<TeamVo> getAllSonTeamByParentTeamList(@Param("list") List<TeamVo> list);

    List<TeamVo> searchTeam(TeamVo teamVo);

    List<TeamVo> searchTeamByUserUuidAndLevelList(@Param("userUuid") String userUuid, @Param("list") List<String> level);

    List<ValueTextVo> searchTeamForSelect(TeamVo teamVo);

    int searchTeamCount(TeamVo teamVo);

    int searchUserCountByTeamUuid(String uuid);

    List<TeamVo> getTeamByUuidList(List<String> teamUuidList);

    List<String> getTeamUuidListByUserUuid(String userUuid);

    String getTeamLockByUuid(String uuid);

    TeamVo getTeamByParentUuidAndStartNum(@Param("parentUuid") String parentUuid, @Param("startNum") int startNum);

    int checkTeamIsExistsByLeftRightCode(@Param("uuid") String uuid, @Param("lft") int lft, @Param("rht") int rht);

    List<String> getTeamUserUuidListByLftRhtLevelTitle(
            @Param("lft") Integer lft,
            @Param("rht") Integer rht,
            @Param("level") String level,
            @Param("title") String title
    );

    List<String> getTeamUserUuidListByLftRhtTitle(
            @Param("lft") Integer lft,
            @Param("rht") Integer rht,
            @Param("title") String title
    );

    List<TeamVo> getAncestorsAndSelfByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("level") String level);

    List<TeamVo> getTeamUserCountAndChildCountListByUuidList(@Param("list") List<String> teamUuidList, @Param("isActive") Integer isActive);

    List<TeamUserVo> getTeamUserListByTeamUuid(String teamUuid);

    Integer getMaxRhtCode();

    int getTeamCount();

    /**
     * @return int 返回左右编码不正确的个数
     * @Time:2020年7月20日
     * @Description: 判断左右编码是否全部正确，符合下列条件的才正确
     * 1.左右编码不能为null
     * 2.左编码不能小于2，右编码不能小于3
     * 3.子节点的左编码大于父节点的左编码，子节点的右编码小于父节点的右编码
     * 4.没有子节点的节点左编码比右编码小1
     */
//     int checkLeftRightCodeIsWrong();

    List<String> getTeamUuidByName(String name);

    List<ValueTextVo> getTeamUuidAndNameMapList(List<String> list);

    List<TeamVo> getRepeatTeamNameByNameList(List<String> list);

    List<String> getChildrenUuidListByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht);

    List<TeamUserTitleVo> getTeamUserTitleListByTeamUuid(String teamUuid);

//	 int deleteTeamByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht);

    int insertTeam(TeamVo teamVo);

    int insertTeamUser(TeamUserVo teamUserVo);

    int insertTeamUserTitle(@Param("teamUuid") String teamUuid, @Param("userUuid") String userUuid, @Param("titleId") Long titleId);

    int updateTeamNameByUuid(TeamVo teamVo);

    int updateTeamParentUuidByUuid(TeamVo teamVo);

    int updateTeamParentUuidAndNameByUuid(TeamVo teamVo);

    int updateTeamLeftRightCode(@Param("uuid") String uuid, @Param("lft") int lft, @Param("rht") int rht);

    int updateTeamUserTitle(TeamUserVo teamUserVo);

//	 int batchUpdateTeamLeftCode(@Param("minCode")Integer minCode, @Param("step") int step);

//	 int batchUpdateTeamRightCode(@Param("minCode")Integer minCode, @Param("step") int step);

    int batchUpdateTeamLeftRightCodeByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("step") int step);

    int deleteTeamUserByTeamUuid(String teamUuid);

    int deleteTeamUserByTeamUuidList(List<String> teamUuidList);

    int deleteTeamUserByTeamUuidAndUserUuidList(@Param("teamUuid") String teamUuid, @Param("userUuidList") List<String> userUuidList);

    int deleteTeamByUuidList(List<String> uuidList);

    int deleteTeamRoleByTeamUuidList(List<String> teamUuidList);

    int deleteTeamUserTitleByTeamUuidAndTitle(@Param("teamUuid") String teamUuid, @Param("titleId") Long titleId);

    int checkTitleIsReferenceByTitleId(Long id);
}
