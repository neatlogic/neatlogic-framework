package codedriver.framework.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import codedriver.framework.asynchronization.threadlocal.TenantContext;

public class CodeDriverRoutingDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() {
		if (TenantContext.get() != null && StringUtils.isNotBlank(TenantContext.get().getTenantUuid())) {
			return TenantContext.get().getTenantUuid();
		} else {
			return null;
		}
	}
}
