package codedriver.framework.asynchronization.threadlocal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codedriver.framework.dto.ModuleVo;

public class TenantContext implements Serializable {
	private static final long serialVersionUID = -5977938340288247600L;
	private static ThreadLocal<TenantContext> instance = new ThreadLocal<TenantContext>();
	private String tenantUuid;
	private Boolean useDefaultDatasource = false;
	private List<ModuleVo> activeModuleList;
	private Map<String, ModuleVo>  activeModuleMap;
	public static TenantContext init() {
		TenantContext context = new TenantContext();
		instance.set(context);
		return context;
	}
	
	public static TenantContext init(TenantContext _tenantContext) {
		TenantContext context = new TenantContext();
		if(_tenantContext != null) {
			context.setTenantUuid(_tenantContext.getTenantUuid());
			context.setActiveModuleList(_tenantContext.getActiveModuleList());
		}
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

	public TenantContext setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
		return this;
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

	public List<ModuleVo> getActiveModuleList() {
		return activeModuleList;
	}

	public void setActiveModuleList(List<ModuleVo> activeModuleList) {
		this.activeModuleList = activeModuleList;
		activeModuleMap = new HashMap<>();
		if(activeModuleList != null && activeModuleList.size() > 0) {
			for(ModuleVo module : activeModuleList) {
				activeModuleMap.put(module.getId(), module);
			}
		}
	}

	public Map<String, ModuleVo> getActiveModuleMap() {
		return activeModuleMap;
	}
	
	public boolean containsModule(String moduleId) {
		return activeModuleMap.containsKey(moduleId);
	}
}
