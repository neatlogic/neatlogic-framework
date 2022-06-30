/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.worktime.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.worktime.dto.WorktimeRangeVo;
import codedriver.framework.worktime.dto.WorktimeVo;
import org.apache.ibatis.annotations.Param;

public interface WorktimeMapper {

    public WorktimeVo getWorktimeByUuid(String uuid);

    public WorktimeVo getWorktimeByName(String name);

    public int checkWorktimeNameIsRepeat(WorktimeVo worktimeVo);

    public int checkWorktimeIsExists(String uuid);

    public int searchWorktimeCount(WorktimeVo worktimeVo);

    public List<WorktimeVo> searchWorktimeList(WorktimeVo worktimeVo);

    public List<ValueTextVo> searchWorktimeListForSelect(WorktimeVo worktimeVo);

    public List<WorktimeRangeVo> getWorktimeRangeListByWorktimeUuid(String worktimeUuid);

    public List<String> getWorktimeDateList(WorktimeRangeVo worktimeRangeVo);

    public WorktimeRangeVo getRecentWorktimeRange(WorktimeRangeVo worktimeRangeVo);

    public WorktimeRangeVo getRecentWorktimeRangeBackward(WorktimeRangeVo worktimeRangeVo);

    public long calculateCostTime(@Param("worktimeUuid")
                                          String worktimeUuid, @Param("startTime")
                                          long startTime, @Param("endTime")
                                          long endTime);

    public int checkIsWithinWorktimeRange(@Param("worktimeUuid") String worktimeUuid, @Param("value") long value);

    public int checkIsWithinWorktime(@Param("worktimeUuid") String worktimeUuid, @Param("date") String date);

    public List<WorktimeVo> getYearListByWorktimeUuidList(List<String> worktimeUuidList);

    List<Integer> getYearListByWorktimeUuid(String worktimeUuid);

    public int insertWorktime(WorktimeVo worktimeVo);

    public int insertBatchWorktimeRange(List<WorktimeRangeVo> worktimeRangeList);

    public int updateWorktime(WorktimeVo worktimeVo);

    public int updateWorktimeDeleteStatus(WorktimeVo worktimeVo);

    public int deleteWorktimeByUuid(String uuid);

    public int deleteWorktimeRange(WorktimeRangeVo worktimeRangeVo);
}
