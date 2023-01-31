/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
