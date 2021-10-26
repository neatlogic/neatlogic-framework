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

    RunnerGroupVo getRunnerGroupById(Long groupId);

    RunnerVo getRunnerByIp(String requestIp);

    RunnerVo getRunnerByIpAndPort(@Param("host") String host, @Param("port") String port);

    RunnerVo getRunnerById(Long runnerId);

    List<RunnerVo> searchRunner(RunnerGroupVo runnerGroupVo);

    int searchRunnerGroupCount();

    List<RunnerGroupVo> searchRunnerGroup(RunnerGroupVo groupVo);

    int searchRunnerCount(Long id);

    int checkGroupNameIsRepeats(RunnerGroupVo runnerGroupVo);

    int checkRunnerIdIsExist(Long id);

    int checkRunnerGroupIdIsExist(Long id);

    int checkRunnerNameIsExistByName(RunnerVo runnerVo);

    int checkRunnerNameIsExist(RunnerVo runnerVo);

    Integer insertRunnerMap(RunnerMapVo runnerMapVo);

    void insertRunnerGroup(RunnerGroupVo runnerGroupVo);

    void insertNetwork(GroupNetworkVo networkVo);

    void insertRunner(RunnerVo runnerVo);

    void updateRunnerGroup(RunnerGroupVo runnerGroupVo);

    void updateRunner(RunnerVo runnerVo);

    void deleteGroupNetWork(Long id);

    void deleteRunnerGroupById(Long id);

    void deleteRunnerById(Long id);

}
