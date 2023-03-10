package neatlogic.framework.dao.mapper;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dto.TeamUserTitleVo;
import neatlogic.framework.dto.TeamUserVo;
import neatlogic.framework.dto.TeamVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeamMapper {
    TeamVo getTeam(TeamVo teamVo);

    TeamVo getTeamSimpleInfoByUuid(TeamVo teamVo);

    int checkTeamIsExists(String uuid);

    List<String> getTeamUuidListByUuidList(List<String> uuidList);

    TeamVo getTeamByUuid(String uuid);

    List<TeamVo> getTeamByParentUuid(String parentUuid);

    List<TeamVo> getTeamUuidByLevelListAndTeamList(@Param("teamList") List<TeamVo> teamList,@Param("levelList") List<String> levelList);

    List<TeamVo> getAllSonTeamByParentTeamList(@Param("list") List<TeamVo> list);

    List<TeamVo> searchTeam(TeamVo teamVo);

    List<TeamVo> searchTeamOrderByNameLengthForSelect(TeamVo teamVo);

    List<TeamVo> searchTeamByUserUuidAndLevelList(@Param("userUuid") String userUuid, @Param("list") List<String> level);

    List<ValueTextVo> searchTeamForSelect(TeamVo teamVo);

    int searchTeamCount(TeamVo teamVo);

    int searchUserCountByTeamUuid(String uuid);

    List<TeamVo> getTeamByUuidList(List<String> teamUuidList);

    List<String> getTeamUuidListByUserUuid(String userUuid);

    List<TeamVo> getTeamListByUserUuid(String userUuid);

    List<TeamVo> getTeamListByUserUuidList(List<String> userUuidList);

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

    List<String> getUpwardTeamNameListByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht);

//    List<TeamVo> getTeamUserCountAndChildCountListByUuidList(@Param("list") List<String> teamUuidList, @Param("isActive") Integer isActive);

    List<TeamVo> getTeamUserCountListByUuidList(@Param("list") List<String> teamUuidList, @Param("isActive") Integer isActive);

    List<TeamVo> getChildCountListByUuidList(List<String> teamUuidList);

    List<TeamUserVo> getTeamUserListByTeamUuid(String teamUuid);

    Integer getMaxRhtCode();

    int getTeamCount();

    /**
     * @return int ????????????????????????????????????
     * @Time:2020???7???20???
     * @Description: ?????????????????????????????????????????????????????????????????????
     * 1.?????????????????????null
     * 2.?????????????????????2????????????????????????3
     * 3.???????????????????????????????????????????????????????????????????????????????????????????????????
     * 4.????????????????????????????????????????????????1
     */
//     int checkLeftRightCodeIsWrong();

    List<String> getTeamUuidByName(String name);

    List<ValueTextVo> getTeamUuidAndNameMapList(List<String> list);

    public List<TeamVo> getRepeatTeamNameByNameList(List<String> list);

    List<String> getChildrenUuidListByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht);

    List<TeamUserTitleVo> getTeamUserTitleListByTeamUuid(String teamUuid);

    List<TeamUserTitleVo> getTeamUserTitleListByTeamUuidList(@Param("teamUuidList") List<String> teamUuidList);

    List<TeamUserTitleVo> getTeamUserTitleListByTeamlrAndTitleId(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("titleId") Long titleId);

    List<TeamUserTitleVo> getTeamUserTitleListByUserUuid(String userUuid);

    int checkTitleIsReferenceByTitleId(Long id);

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

    int updateUpwardUuidPathByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht);

    int updateUpwardNamePathByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht);

    int deleteTeamUserByTeamUuid(String teamUuid);

    int deleteTeamUserByTeamUuidList(List<String> teamUuidList);

    int deleteTeamUserByTeamUuidAndUserUuidList(@Param("teamUuid") String teamUuid, @Param("userUuidList") List<String> userUuidList);

    int deleteTeamByUuidList(List<String> uuidList);

    int deleteTeamRoleByTeamUuidList(List<String> teamUuidList);

    int deleteTeamUserTitleByTeamUuidAndTitle(@Param("teamUuid") String teamUuid, @Param("titleId") Long titleId);

    int deleteTeamUserTitleByTeamUuid(String uuid);

}
