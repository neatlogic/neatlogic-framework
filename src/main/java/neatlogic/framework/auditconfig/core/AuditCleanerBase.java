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

package neatlogic.framework.auditconfig.core;

import neatlogic.framework.auditconfig.dao.mapper.AuditConfigMapper;
import neatlogic.framework.auditconfig.dto.AuditConfigVo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

public abstract class AuditCleanerBase implements IAuditCleaner {

    @Resource
    private AuditConfigMapper auditConfigMapper;

    public final void clean() throws Exception {
        AuditConfigVo auditConfigVo = auditConfigMapper.getAuditConfigByName(this.getName());
        if (auditConfigVo != null && MapUtils.isNotEmpty(auditConfigVo.getConfig())) {
            int timeRange = auditConfigVo.getConfig().getIntValue("timeRange");
            String timeUnit = auditConfigVo.getConfig().getString("timeUnit");
            if (timeRange > 0 && StringUtils.isNotBlank(timeUnit)) {
                int dayBefore = 0;
                switch (timeUnit) {
                    case "day":
                        dayBefore = timeRange;
                        break;
                    case "week":
                        dayBefore = timeRange * 7;
                        break;
                    case "month":
                        dayBefore = timeRange * 30;
                        break;
                    case "year":
                        dayBefore = timeRange * 365;
                        break;
                }
                if (dayBefore > 0) {
                    this.myClean(dayBefore);
                }
            }

        }
    }

    protected abstract void myClean(int dayBefore) throws Exception;
}
