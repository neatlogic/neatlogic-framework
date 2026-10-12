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

package neatlogic.framework.dto.healthcheck;

/**
 * 冻结在快照采集时的任务耗时，防止历史样本随系统时间变化。
 */
public class ThreadSnapshotTaskVo extends ThreadTaskVo {
    private long capturedTimeCost;

    /** 获取采集时的任务运行耗时。 */
    @Override
    public long getTimeCost() {
        return capturedTimeCost;
    }

    /** 设置采集时的任务运行耗时。 */
    public void setTimeCost(long timeCost) {
        this.capturedTimeCost = timeCost;
    }
}
