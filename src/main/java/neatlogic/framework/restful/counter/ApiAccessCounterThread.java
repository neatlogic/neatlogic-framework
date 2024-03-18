/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.restful.counter;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
