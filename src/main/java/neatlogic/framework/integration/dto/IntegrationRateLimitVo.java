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

package neatlogic.framework.integration.dto;

import java.util.Date;

public class IntegrationRateLimitVo {

    private String integrationUuid;
    private Date windowStartTime;
    private Integer counter;

    public IntegrationRateLimitVo() {
    }

    public IntegrationRateLimitVo(String integrationUuid) {
        this.integrationUuid = integrationUuid;
    }

    public String getIntegrationUuid() {
        return integrationUuid;
    }

    public void setIntegrationUuid(String integrationUuid) {
        this.integrationUuid = integrationUuid;
    }

    public Date getWindowStartTime() {
        return windowStartTime;
    }

    public void setWindowStartTime(Date windowStartTime) {
        this.windowStartTime = windowStartTime;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }
}
