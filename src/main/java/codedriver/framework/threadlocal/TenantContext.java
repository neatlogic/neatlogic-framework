package codedriver.framework.threadlocal;

import java.io.Serializable;

public class TenantContext implements Serializable {
	private static ThreadLocal<TenantContext> instance = new ThreadLocal<TenantContext>();
	private String tenantUuid;
	private Boolean useDefaultDatasource = false;

	public static TenantContext init() {
		TenantContext context = new TenantContext();
		instance.set(context);
		return context;
	}

	public static TenantContext init(String _tenantUuid) {
		TenantContext context = new TenantContext(_tenantUuid);
		instance.set(context);
		return context;
	}

	private TenantContext() {

	}

	private TenantContext(String _tenantUuid) {
		this.tenantUuid = _tenantUuid;
	}

	public String getTenantUuid() {
		if (useDefaultDatasource) {
			return null;
		} else {
			return tenantUuid;
		}
	}

	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}

	public static TenantContext get() {
		return instance.get();
	}

	public void release() {
		instance.remove();
	}

	public Boolean getUseDefaultDatasource() {
		return useDefaultDatasource;
	}

	public void setUseDefaultDatasource(Boolean useDefaultDatasource) {
		this.useDefaultDatasource = useDefaultDatasource;
	}
}
