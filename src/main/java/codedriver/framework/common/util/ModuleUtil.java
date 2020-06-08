package codedriver.framework.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.dto.ModuleGroupVo;
import codedriver.framework.dto.ModuleVo;

public class ModuleUtil {
	static Logger logger = LoggerFactory.getLogger(ModuleUtil.class);
	private static List<ModuleVo> moduleList = new ArrayList<>();
	private static Map<String, ModuleVo> moduleMap = new HashMap<>();

	public static List<ModuleVo> getAllModuleList() {
		return moduleList;
	}

	public static ModuleVo getModuleById(String moduleId) {
		return moduleMap.get(moduleId);
	}

	public static void addModule(ModuleVo moduleVo) {
		moduleMap.put(moduleVo.getId(), moduleVo);
		moduleList.add(moduleVo);
	}


	public static List<ModuleVo> getTenantActiveModuleList(List<String> tenantModuleList) {
		List<ModuleVo> returnList = new ArrayList<>();
		if (!tenantModuleList.contains("framework")) {
			tenantModuleList.add("framework");
		}
		ModuleVo module = null;
		for (String moduleId : tenantModuleList) {
			module = moduleMap.get(moduleId);
			if (module != null) {
				returnList.add(module);
			}
		}
		return returnList;
	}
	
	public static ModuleGroupVo getModuleGroup(String group) {
		ModuleGroupVo moduleGroup = null;
		Set<Entry<String, ModuleVo>> moduleSet = moduleMap.entrySet();
		for(Entry<String, ModuleVo> moduleEntry :moduleSet) {
			ModuleVo moduleVo = moduleEntry.getValue();
			if(moduleVo.getGroup().equals(group)) {
				if(moduleGroup == null) {
					moduleGroup = new ModuleGroupVo();
					moduleGroup.setGroup(moduleVo.getGroup());
					moduleGroup.setGroupName(moduleVo.getGroupName());
					moduleGroup.setGroupDescription(moduleVo.getGroupDescription());
					moduleGroup.setGroupSort(moduleVo.getGroupSort());
					List<ModuleVo> moduleList = new ArrayList<ModuleVo>();
					moduleList.add(moduleVo);
					moduleGroup.setModuleList(moduleList);
				}else {
					moduleGroup.getModuleList().add(moduleVo);
				}
			}
		}
		return moduleGroup;
	}
	
	public static Map<String,ModuleGroupVo> getModuleGroupMap() {
		Map<String,ModuleGroupVo> moduleGroupMap = new HashMap<String,ModuleGroupVo>();
		Set<Entry<String, ModuleVo>> moduleSet = moduleMap.entrySet();
		for(Entry<String, ModuleVo> moduleEntry :moduleSet) {
			ModuleVo moduleVo = moduleEntry.getValue();
			ModuleGroupVo moduleGroupVo = null;
			if(moduleGroupMap.containsKey(moduleVo.getGroup())) {
				moduleGroupVo = (ModuleGroupVo) moduleGroupMap.get(moduleVo.getGroup());
				moduleGroupVo.getModuleList().add(moduleVo);
			}else {
				moduleGroupVo = new ModuleGroupVo();
				moduleGroupVo.setGroup(moduleVo.getGroup());
				moduleGroupVo.setGroupName(moduleVo.getGroupName());
				moduleGroupVo.setGroupDescription(moduleVo.getGroupDescription());
				moduleGroupVo.setGroupSort(moduleVo.getGroupSort());
				List<ModuleVo> moduleList = new ArrayList<ModuleVo>();
				moduleList.add(moduleVo);
				moduleGroupVo.setModuleList(moduleList);
			}
			moduleGroupMap.put(moduleGroupVo.getGroup(), moduleGroupVo);
			
		}
		return moduleGroupMap;
	}
}
