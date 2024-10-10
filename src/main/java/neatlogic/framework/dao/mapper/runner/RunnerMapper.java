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

package neatlogic.framework.dao.mapper.runner;


import neatlogic.framework.dto.runner.*;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RunnerMapper {

    List<GroupNetworkVo> getAllNetworkMask(List<Long> runnerGroupIdList);

    List<RunnerMapVo> getAllRunnerMap(List<Long> runnerGroupIdList);

    List<RunnerVo> getRunnerListByGroupId(Long runnerGroupId);

    RunnerGroupVo getRunnerMapGroupById(Long groupId);

    RunnerVo getRunnerByIp(String requestIp);

    RunnerVo getRunnerByNettyIpAndNettyPort(@Param("nettyIp") String nettyIp, @Param("nettyPort") String nettyPort);

    RunnerVo getRunnerById(Long runnerId);

    RunnerVo getRunnerByName(String name);

    List<RunnerMapVo> getRunnerByRunnerMapIdList(List<Long> runnerMapIdList);

    RunnerMapVo getRunnerMapByRunnerMapId(Long runnerId);

    List<RunnerVo> searchRunner(RunnerVo runnerVo);

    List<RunnerVo> getRunnerListByIdSet(@Param("runnerIdSet") Set<Long> runnerIdSet);

    int searchRunnerGroupCount(RunnerGroupVo groupVo);

    List<RunnerGroupVo> searchRunnerGroup(RunnerGroupVo groupVo);

    RunnerGroupVo getRunnerGroupById(Long id);

    List<RunnerGroupVo> getRunnerGroupByIdList(List<Long> idlist);

    RunnerGroupVo getRunnerGroupByName(String string);

    List<RunnerGroupVo> getRunnerGroupByTagIdOrNameList(List<String> tagList);

    List<RunnerMapVo> getRunnerMapListByRunnerGroupId(Long runnerGroupId);

    List<Map<String, Object>> searchRunnerGroupForMatrix(MatrixDataVo matrixDataVo);

    int searchRunnerCountByGroupId(Long id);

    int searchRunnerGroupCountForMatrix(MatrixDataVo matrixDataVo);

    int searchRunnerCount(RunnerVo runnerVo);

    int checkGroupNameIsRepeats(RunnerGroupVo runnerGroupVo);

    int checkRunnerIdIsExist(Long id);

    int checkRunnerGroupIdIsExist(Long id);

    int checkRunnerNameIsExist(RunnerVo runnerVo);

    int checkRunnerIsExistByIdAndIp(@Param("id") Long id, @Param("host") String host);

    int checkRunnerIsUsedByJob(Long id);

    int checkRunnerIsUsedByRunnerGroup(Long id);

    Integer insertRunnerMap(RunnerMapVo runnerMapVo);

    void insertRunnerGroup(RunnerGroupVo runnerGroupVo);

    void insertNetwork(GroupNetworkVo networkVo);

    void insertRunnerTag(GroupTagVo groupTagVo);

    void replaceRunner(RunnerVo runnerVo);

    void insertRunnerGroupRunnerByRunnerIdListAndGroupId(@Param("runnerIdList") List<Long> runnerIdList, @Param("groupId") Long groupId);

    void updateRunnerGroup(RunnerGroupVo runnerGroupVo);

    void updateRunner(RunnerVo runnerVo);

    int updateRunnerHost(@Param("runnerHost") String runnerHost, @Param("url") String url);

    void deleteGroupNetWork(Long id);

    void deleteRunnerGroupById(Long id);

    void deleteRunnerById(Long id);

    void deleteRunnerGroupRunnerByGroupId(Long groupId);

    void deleteRunnerGroupRunnerByRunnerId(Long runnerId);

    void deleteRunnerGroupRunnerByGroupIdAndRunnerId(@Param("groupId") Long groupId, @Param("runnerId") Long runnerId);

    void deleteGroupTag(Long groupId);

}
