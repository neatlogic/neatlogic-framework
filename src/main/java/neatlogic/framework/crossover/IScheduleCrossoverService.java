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

package neatlogic.framework.crossover;

import neatlogic.framework.scheduler.dto.JobLoadVo;

import java.util.List;

public interface IScheduleCrossoverService extends ICrossoverService {
    /**
     * 定时作业测试
     *
     * @param jobHandlerClassName 作业处理器类名
     * @param jobUuid             作业uuid
     * @param type                类型 private ｜ public
     */
    void scheduleTest(String jobHandlerClassName, String jobUuid, String type) throws Exception;

    List<JobLoadVo> getJobLoadList(String jobName, String jobGroup);
}
