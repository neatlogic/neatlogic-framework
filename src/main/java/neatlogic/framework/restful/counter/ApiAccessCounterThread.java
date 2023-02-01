/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.restful.counter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;

/**
 * @Time:2020年7月15日
 * @ClassName: ApiAccessCounterThread
 * @Description: 模拟接口访问任务类
 */
public class ApiAccessCounterThread extends NeatLogicThread {

    private static final Logger logger = LoggerFactory.getLogger(ApiAccessCounterThread.class);

    private final String tenantUuid;

    private final String token;

    public ApiAccessCounterThread(int i, String tenantUuid, String token) {
        super("API-ACCESS-COUNTER" + i);
        this.tenantUuid = tenantUuid;
        this.token = token;
    }

    @Override
    protected void execute() {
        try {
            if (StringUtils.isNotBlank(tenantUuid) && StringUtils.isNotBlank(token)) {
                TenantContext.init(tenantUuid);
                ApiAccessCountUpdateThread.putToken(token);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
