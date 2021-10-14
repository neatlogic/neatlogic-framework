/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.mapper.runner;


import codedriver.framework.dto.runner.GroupNetworkVo;
import codedriver.framework.dto.runner.RunnerGroupVo;
import codedriver.framework.dto.runner.RunnerMapVo;
import codedriver.framework.dto.runner.RunnerVo;

import java.util.List;

public interface RunnerMapper {

    List<GroupNetworkVo> getAllNetworkMask();

    RunnerGroupVo getRunnerGroupById(Long groupId);

    List<RunnerMapVo> getAllRunnerMap();

    RunnerVo getRunnerById(Long runnerId);

    Integer insertRunnerMap(RunnerMapVo runnerMapVo);

    int checkGroupNameIsRepeats(RunnerGroupVo runnerGroupVo);

    void updateRunnerGroup(RunnerGroupVo runnerGroupVo);

    void insertRunnerGroup(RunnerGroupVo runnerGroupVo);

    void deleteGroupNetWork(Long id);

    void insertNetwork(GroupNetworkVo networkVo);

    int checkRunnerGroupIdIsExist(Long id);

    void deleteRunnerGroupById(Long id);

    int checkRunnerNameIsExist(RunnerVo runnerVo);

    void updateRunner(RunnerVo runnerVo);

    void insertRunner(RunnerVo runnerVo);

    int checkRunnerIdIsExist(Long id);

    void deleteRunnerById(Long id);

    int checkRunnerNameIsExistByName(RunnerVo runnerVo);

    RunnerVo getRunnerByIp(String requestIp);

    List<RunnerVo> getRunnerByGroupId(Long runnerGroupId);

    RunnerVo getRunnerByIpAndPort(String host, String port);

    List<RunnerVo> searchRunnerByRunnerGroup(RunnerGroupVo runnerGroupVo);

    int searchRunnerCount(Long id);
}
