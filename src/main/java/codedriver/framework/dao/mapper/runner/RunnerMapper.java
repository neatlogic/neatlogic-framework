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
}
