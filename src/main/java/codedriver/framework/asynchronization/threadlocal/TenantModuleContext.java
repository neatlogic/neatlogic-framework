package codedriver.framework.asynchronization.threadlocal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codedriver.framework.dto.ModuleVo;
import codedriver.framework.dto.TenantModuleVo;

public class TenantModuleContext implements Serializable {
	private static final long serialVersionUID = -6561702232762771783L;
	private transient static ThreadLocal<TenantModuleContext> instance = new ThreadLocal<TenantModuleContext>();
	private Map<String,List<ModuleVo>> tenantModuleMap = null;
	
	public TenantModuleContext(List<TenantModuleVo> tenantModuleList) {
		this.tenantModuleMap = new HashMap<String,List<ModuleVo>>();
		for(TenantModuleVo tenantModuleVo : tenantModuleList) {
			this.tenantModuleMap.put(tenantModuleVo.getTenantUuid(), tenantModuleVo.getModuleList());
		}
	}

	public static TenantModuleContext init(List<TenantModuleVo> tenantModuleList) {
		TenantModuleContext context = new TenantModuleContext(tenantModuleList);
		instance.set(context);
		return context;
	}
	
	public static TenantModuleContext get() {
		return instance.get();
	}

	public void release() {
		instance.remove();
	}

	public Map<String, List<ModuleVo>> getTenantModuleMap() {
		return tenantModuleMap;
	}

	public void setTenantModuleMap(Map<String, List<ModuleVo>> tenantModuleMap) {
		this.tenantModuleMap = tenantModuleMap;
	}
	
	
}
