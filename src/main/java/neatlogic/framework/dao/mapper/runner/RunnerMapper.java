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

package neatlogic.framework.dao.mapper.runner;


import neatlogic.framework.dto.runner.*;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    RunnerMapVo getRunnerMapByRunnerName(String runnerName);

    List<RunnerMapVo> getRunnerMapByRunnerNameList(List<String> runnerNameList);

    List<RunnerVo> searchRunner(RunnerVo runnerVo);

    List<RunnerVo> getRunnerListByIdSet(@Param("runnerIdSet") Set<Long> runnerIdSet);

    int searchRunnerGroupCount(RunnerGroupVo groupVo);

    List<RunnerGroupVo> searchRunnerGroup(RunnerGroupVo groupVo);

    RunnerGroupVo getRunnerGroupById(Long id);

    RunnerGroupVo getRunnerGroupByIdOrName(String runnerGroup);

    List<RunnerGroupVo> getRunnerGroupByIdList(List<Long> idlist);

    List<RunnerGroupVo> getAllRunnerGroupList();

    RunnerGroupVo getRunnerGroupByName(String string);

    List<RunnerGroupVo> getRunnerGroupByTagIdOrNameList(List<String> tagList);

    List<RunnerMapVo> getRunnerMapListByRunnerGroupId(Long runnerGroupId);

    List<RunnerMapVo> getAllRunnerMapList();

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

    void updateStatusAndInfoByHost(@Param("host") String host, @Param("status") String status, @Param("info") String info);

    void updateStatusById(@Param("id") Long id, @Param("status") String status, @Param("statusLcd") Date statusLcd);

    void deleteGroupNetWork(Long id);

    void deleteRunnerGroupById(Long id);

    void deleteRunnerById(Long id);

    void deleteRunnerGroupRunnerByGroupId(Long groupId);

    void deleteRunnerGroupRunnerByRunnerId(Long runnerId);

    void deleteRunnerGroupRunnerByGroupIdAndRunnerId(@Param("groupId") Long groupId, @Param("runnerId") Long runnerId);

    void deleteGroupTag(Long groupId);

}
