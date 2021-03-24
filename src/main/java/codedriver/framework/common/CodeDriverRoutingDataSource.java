package codedriver.framework.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import codedriver.framework.asynchronization.threadlocal.TenantContext;

public class CodeDriverRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        if (TenantContext.get() != null && StringUtils.isNotBlank(TenantContext.get().getTenantUuid())) {
            String key = TenantContext.get().getTenantUuid();
            if (TenantContext.get().isData()) {
                key += "_data";
            }
            return key;
        } else {
            return null;
        }
    }
}
