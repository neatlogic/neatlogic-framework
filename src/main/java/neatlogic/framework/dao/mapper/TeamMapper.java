package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.TeamUserTitleVo;
import neatlogic.framework.dto.TeamUserVo;
import neatlogic.framework.dto.TeamVo;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TeamMapper {
    TeamVo getTeam(TeamVo teamVo);

    TeamVo getTeamSimpleInfoByUuid(TeamVo teamVo);

    int checkTeamIsExists(String uuid);

    int checkTeamNameIsIsRepeat(TeamVo teamVo);

    List<String> getTeamUuidListByUuidList(List<String> uuidList);

    TeamVo getTeamByUuid(String uuid);

    List<TeamVo> getTeamUuidByLevelListAndTeamList(@Param("teamList") List<TeamVo> teamList, @Param("levelList") List<String> levelList);

    List<TeamVo> getAllSonTeamByParentTeamList(@Param("list") List<TeamVo> list);

    List<TeamVo> searchTeam(TeamVo teamVo);

    List<TeamVo> searchTeamOrderByNameLengthForSelect(TeamVo teamVo);

    List<TeamVo> searchTeamByUserUuidAndLevelList(@Param("userUuid") String userUuid, @Param("list") List<String> level);

    int searchTeamCount(TeamVo teamVo);

    int searchUserCountByTeamUuid(String uuid);

    List<TeamVo> getTeamByUuidList(List<String> teamUuidList);

    List<TeamVo> getTeamListContainsDeletedByUuidList(List<String> teamUuidList);

    List<TeamVo> getTeamByIdList(List<Long> idList);

    List<String> getTeamUuidListByUserUuid(String userUuid);

    List<TeamVo> getTeamListByUserUuid(String userUuid);

    List<TeamVo> getTeamListByUserUuidList(List<String> userUuidList);

    TeamVo getTeamByParentUuidAndStartNum(@Param("parentUuid") String parentUuid, @Param("startNum") int startNum);

    List<TeamVo> getAncestorsAndSelfByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("level") String level);

    List<TeamVo> getTeamUserCountListByUuidList(@Param("list") List<String> teamUuidList, @Param("isActive") Integer isActive);

    List<TeamVo> getChildCountListByUuidList(List<String> teamUuidList);

    List<TeamUserVo> getTeamUserListByTeamUuid(String teamUuid);

    List<String> getTeamUuidByName(String name);

    TeamVo getTeamByNameAndParentUuid(TeamVo teamVo);

    List<TeamVo> getRepeatTeamNameByNameList(List<String> list);

    List<String> getChildrenUuidListByLeftRightCode(@Param("lft") Integer lft, @Param("rht") Integer rht);

    List<TeamUserTitleVo> getTeamUserTitleListByTeamUuid(String teamUuid);

    List<TeamUserTitleVo> getTeamUserTitleListByTeamlrAndTitleId(@Param("lft") Integer lft, @Param("rht") Integer rht, @Param("titleId") Long titleId);

    List<TeamUserTitleVo> getTeamUserTitleListByUserUuid(String userUuid);

    String getTeamUuidByUpwardNamePath(String upwardNamePath);

    int checkTitleIsReferenceByTitleId(Long id);

    int searchTeamCountForMatrix(MatrixDataVo searchVo);

    List<Map<String, Object>> searchTeamListForMatrix(MatrixDataVo searchVo);

    int insertTeam(TeamVo teamVo);

    void insertTeamForLdap(TeamVo teamVo);

    int insertTeamUser(TeamUserVo teamUserVo);

    int insertTeamUserTitle(@Param("teamUuid") String teamUuid, @Param("userUuid") String userUuid, @Param("titleId") Long titleId);

    int updateTeamNameByUuid(TeamVo teamVo);

    int updateTeamIsDeleteBySourceAndLcd(@Param("source") String source, @Param("lcd") Date lcd);

    int updateUpwardUuidPathByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht);

    int updateUpwardNamePathByLftRht(@Param("lft") Integer lft, @Param("rht") Integer rht);

    int updateTeamIsDeletedByUuidList(List<String> uuidList);

    int deleteTeamUserByTeamUuid(String teamUuid);

    int deleteTeamUserByTeamUuidList(List<String> teamUuidList);

    int deleteTeamUserByTeamUuidAndUserUuidList(@Param("teamUuid") String teamUuid, @Param("userUuidList") List<String> userUuidList);

//    int deleteTeamByUuidList(List<String> uuidList);

    int deleteTeamRoleByTeamUuidList(List<String> teamUuidList);

    int deleteTeamUserTitleByTeamUuid(String uuid);
}
