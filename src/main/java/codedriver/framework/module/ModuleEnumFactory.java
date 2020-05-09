package codedriver.framework.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import codedriver.framework.common.constvalue.IModuleEnum;

public class ModuleEnumFactory {
	private static Map<String, IModuleEnum> moduleEnumMap = new HashMap<String,IModuleEnum>();
	static {
		Reflections reflections = new Reflections("codedriver");
		Set<Class<? extends IModuleEnum>> moduleEnumClassSet = reflections.getSubTypesOf(IModuleEnum.class);
		for (Class<? extends IModuleEnum> c: moduleEnumClassSet) {
			try {
				Object[] objects = c.getEnumConstants();
				IModuleEnum moduleEnum = (IModuleEnum) objects[0];
				moduleEnumMap.put(moduleEnum.getValue(), moduleEnum);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static Map<String,IModuleEnum> getModuleEnumMap() {
		return moduleEnumMap;
	}
}
