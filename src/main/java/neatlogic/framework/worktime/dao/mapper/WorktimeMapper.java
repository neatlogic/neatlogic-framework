/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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
