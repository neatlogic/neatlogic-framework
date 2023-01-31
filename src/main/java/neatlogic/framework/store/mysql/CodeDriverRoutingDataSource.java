/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.store.mysql;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class CodeDriverRoutingDataSource extends AbstractRoutingDataSource {
    private final Logger logger = LoggerFactory.getLogger(CodeDriverRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        if (TenantContext.get() != null && StringUtils.isNotBlank(TenantContext.get().getTenantUuid())) {
            if (logger.isInfoEnabled()) {
                logger.info("ThreadName:" + Thread.currentThread().getName() + " Tenant:" + TenantContext.get().getTenantUuid());
            }
            String key = TenantContext.get().getTenantUuid();
            if (TenantContext.get().isData()) {
                key += "_DATA";
            }
            return key;
        } else {
            return null;
        }
    }
}
