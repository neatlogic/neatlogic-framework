/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.worktime.dao.mapper;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.worktime.dto.WorktimeRangeVo;
import neatlogic.framework.worktime.dto.WorktimeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
