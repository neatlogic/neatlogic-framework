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

package neatlogic.framework.dto.runner;

/**
 * @author lvzk
 * @since 2021/6/16 19:00
 **/
public class RunnerMapVo extends RunnerVo {
    private static final long serialVersionUID = 6043891449299751917L;
    private Long runnerMapId;

    public RunnerMapVo() {

    }

    public RunnerMapVo(Long runnerMapId, Long runnerId) {
        this.runnerMapId = runnerMapId;
        super.setId(runnerId);
    }

    public RunnerMapVo(String runnerUrl, Long runnerMapId) {
        this.runnerMapId = runnerMapId;
        super.setUrl(runnerUrl);
    }

    public Long getRunnerMapId() {
        return runnerMapId;
    }

    public void setRunnerMapId(Long runnerMapId) {
        this.runnerMapId = runnerMapId;
    }
}
