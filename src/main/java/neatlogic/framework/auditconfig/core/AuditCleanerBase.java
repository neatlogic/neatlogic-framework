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
