/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.worktime.dao.mapper;

import java.util.List;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.worktime.dto.WorktimeRangeVo;
import neatlogic.framework.worktime.dto.WorktimeVo;
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
