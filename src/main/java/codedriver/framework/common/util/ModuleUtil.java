package codedriver.framework.common.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.dto.ModuleVo;

public class ModuleUtil {
	static Logger logger = LoggerFactory.getLogger(ModuleUtil.class);
	private static List<ModuleVo> moduleList = new ArrayList<>();

	public static List<ModuleVo> getAllModuleList() {
		return moduleList;
	}

	public static void addModule(ModuleVo moduleVo) {
		moduleList.add(moduleVo);
	}

	public static List<ModuleVo> getTenantActionModuleList(List<String> tenantModuleList) {
		List<ModuleVo> returnList = new ArrayList<>();
		if (!tenantModuleList.contains("framework")) {
			tenantModuleList.add("framework");
		}
		for (String m : tenantModuleList) {
			for (ModuleVo module : moduleList) {
				if (module.getId().equalsIgnoreCase(m)) {
					returnList.add(module);
					break;
				}
			}
		}
		return returnList;
	}
}
