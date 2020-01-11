package codedriver.framework.common;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import codedriver.framework.asynchronization.threadlocal.TenantContext;

public class CodeDriverRoutingDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() {
		if (TenantContext.get() != null) {
			return TenantContext.get().getTenantUuid();
		} else {
			return null;
		}
	}
}
