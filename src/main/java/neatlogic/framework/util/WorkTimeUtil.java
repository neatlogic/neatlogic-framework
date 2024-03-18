/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.util;

import neatlogic.framework.worktime.dao.mapper.WorktimeMapper;
import neatlogic.framework.worktime.dto.WorktimeRangeVo;
import neatlogic.framework.worktime.exception.WorktimeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class WorkTimeUtil {

    private static WorktimeMapper worktimeMapper;

    @Autowired
    public void setWorktimeMapper(WorktimeMapper _worktimeMapper) {
        worktimeMapper = _worktimeMapper;
    }
    /**
     * 
    * @Time:2020年11月17日
    * @Description: 超时前，计算超时时间点
    * @param activeTime 当前时间
    * @param timeLimit 剩余时长
    * @param worktimeUuid 时间窗口uuid
    * @return long
     */
    public static long calculateExpireTime(long activeTime, long timeLimit, String worktimeUuid) {
        if (worktimeMapper.checkWorktimeIsExists(worktimeUuid) == 0) {
            throw new WorktimeNotFoundException(worktimeUuid);
        }
        if (timeLimit <= 0) {
            return activeTime;
        }
        WorktimeRangeVo worktimeRangeVo = new WorktimeRangeVo();
        WorktimeRangeVo recentWorktimeRange = null;
        long startTime = 0;
        long endTime = 0;
        long duration = 0;
        while (true) {
            worktimeRangeVo.setWorktimeUuid(worktimeUuid);
            worktimeRangeVo.setStartTime(activeTime);
            recentWorktimeRange = worktimeMapper.getRecentWorktimeRange(worktimeRangeVo);
            if (recentWorktimeRange == null) {
                return activeTime;
            }
            startTime = recentWorktimeRange.getStartTime();
            endTime = recentWorktimeRange.getEndTime();
            if (startTime > activeTime) {
                activeTime = startTime;
            }
            duration = endTime - activeTime;
            if (duration >= timeLimit) {
                return activeTime + timeLimit;
            } else {
                timeLimit -= duration;
                activeTime = endTime;
            }
        }
    }
    /**
     * 
    * @Time:2020年11月17日
    * @Description: 超时后，计算超时时间点
    * @param currentTimeMillis
    * @param timeoutPeriod 已超时时长
    * @param worktimeUuid
    * @return long
     */
    public static long calculateExpireTimeForTimedOut(long currentTimeMillis, long timeoutPeriod, String worktimeUuid) {
        if (worktimeMapper.checkWorktimeIsExists(worktimeUuid) == 0) {
            throw new WorktimeNotFoundException(worktimeUuid);
        }
        if (timeoutPeriod <= 0) {
            return currentTimeMillis;
        }
        WorktimeRangeVo worktimeRangeVo = new WorktimeRangeVo();
        WorktimeRangeVo recentWorktimeRange = null;
        long startTime = 0;
        long endTime = 0;
        long duration = 0;
        while (true) {
            worktimeRangeVo.setWorktimeUuid(worktimeUuid);
            worktimeRangeVo.setStartTime(currentTimeMillis);
            recentWorktimeRange = worktimeMapper.getRecentWorktimeRangeBackward(worktimeRangeVo);
            if (recentWorktimeRange == null) {
                return currentTimeMillis;
            }
            startTime = recentWorktimeRange.getStartTime();
            endTime = recentWorktimeRange.getEndTime();
            if (endTime < currentTimeMillis) {
                currentTimeMillis = endTime;
            }
            duration = currentTimeMillis - startTime;
            if (duration >= timeoutPeriod) {
                return currentTimeMillis - timeoutPeriod;
            } else {
                timeoutPeriod -= duration;
                currentTimeMillis = startTime;
            }
        }
    }
    
    public static long calculateCostTime(List<WorktimeRangeVo> worktimeRangeList) {
        if (worktimeRangeList == null || worktimeRangeList.isEmpty()) {
            return 0L;
        }

        // 先按开始时间从小到大排序
        Collections.sort(worktimeRangeList, new Comparator<WorktimeRangeVo>() {
            @Override
            public int compare(WorktimeRangeVo o1, WorktimeRangeVo o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });
        // 保存删除重复时间后的列表
        List<WorktimeRangeVo> deduplicationList = new ArrayList<>();
        long startTime = -1L;
        long endTime = -1L;
        WorktimeRangeVo worktimeRange = null;
        String worktimeUuid = null;
        for (WorktimeRangeVo worktimeRangeVo: worktimeRangeList) {
            if (startTime == -1L) {
                worktimeUuid = worktimeRangeVo.getWorktimeUuid();
                startTime = worktimeRangeVo.getStartTime();
            }
            if (endTime == -1L) {
                endTime = worktimeRangeVo.getEndTime();
            } else if (endTime >= worktimeRangeVo.getEndTime()) {// 如果上一段时间的结束时间大于等于这段的结束时间，说明上一段包含这段时间范围，抛弃这段

            } else if (endTime >= worktimeRangeVo.getStartTime()) {// 如果上一段时间的结束时间大于等于下一段的开始时间，就拼成一段时间
                endTime = worktimeRangeVo.getEndTime();
            } else {// 保存上一段
                worktimeRange = new WorktimeRangeVo();
                worktimeRange.setStartTime(startTime);
                worktimeRange.setEndTime(endTime);
                deduplicationList.add(worktimeRange);
                // 开始新一段
                startTime = worktimeRangeVo.getStartTime();
                endTime = worktimeRangeVo.getEndTime();
            }
        }
        // 最后一段
        worktimeRange = new WorktimeRangeVo();
        worktimeRange.setStartTime(startTime);
        worktimeRange.setEndTime(endTime);
        deduplicationList.add(worktimeRange);

        long sum = 0L;
        for (WorktimeRangeVo worktimeRangeVo : deduplicationList) {
            sum += worktimeMapper.calculateCostTime(worktimeUuid, worktimeRangeVo.getStartTime(), worktimeRangeVo.getEndTime());
        }
        return sum;
    }
}
