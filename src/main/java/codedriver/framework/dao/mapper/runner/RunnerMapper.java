/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.mapper.runner;


import codedriver.framework.dto.runner.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RunnerMapper {

    List<GroupNetworkVo> getAllNetworkMask();

    List<RunnerMapVo> getAllRunnerMap();

    List<RunnerVo> getRunnerListByGroupId(Long runnerGroupId);

    RunnerGroupVo getRunnerMapGroupById(Long groupId);

    RunnerVo getRunnerByIp(String requestIp);

    RunnerVo getRunnerByIpAndPort(@Param("host") String host, @Param("port") String port);

    RunnerVo getRunnerById(Long runnerId);

    RunnerVo getRunnerByName(String name);

    List<RunnerVo> searchRunner(RunnerVo runnerVo);

    int searchRunnerGroupCount(RunnerGroupVo groupVo);

    List<RunnerGroupVo> searchRunnerGroup(RunnerGroupVo groupVo);

    int searchRunnerCountByGroupId(Long id);

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

    void replaceRunner(RunnerVo runnerVo);

    void insertRunnerGroupRunnerByRunnerIdListAndGroupId(@Param("runnerIdList") List<Long> runnerIdList, @Param("groupId") Long groupId);

    void updateRunnerGroup(RunnerGroupVo runnerGroupVo);

    void updateRunner(RunnerVo runnerVo);

    void deleteGroupNetWork(Long id);

    void deleteRunnerGroupById(Long id);

    void deleteRunnerById(Long id);

    void deleteRunnerGroupRunnerByGroupId(Long groupId);

    void deleteRunnerGroupRunnerByRunnerId(Long runnerId);

    void deleteRunnerGroupRunnerByGroupIdAndRunnerId(@Param("groupId") Long groupId, @Param("runnerId") Long runnerId);

    void deleteRuunerByName(String name);
}
