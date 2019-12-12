package codedriver.framework.file.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.file.dto.FileTypeVo;

@RootComponent
public class FileTypeHandlerFactory implements ApplicationListener<ContextRefreshedEvent> {

	private static final Map<String, IFileTypeHandler> componentMap = new HashMap<>();
	private static final List<FileTypeVo> fileTypeList = new ArrayList<>();

	public static IFileTypeHandler getHandler(String type) {
		type = type.toUpperCase();
		return componentMap.get(type);
	}

	public static List<FileTypeVo> getActiveFileTypeHandler() {
		TenantContext tenantContext = TenantContext.get();
		List<ModuleVo> moduleList = tenantContext.getActiveModuleList();
		List<FileTypeVo> returnProcessStepHandlerList = new ArrayList<>();
		for (FileTypeVo fileTypeHandler : fileTypeList) {
			for (ModuleVo moduleVo : moduleList) {
				if (moduleVo.getId().equalsIgnoreCase(fileTypeHandler.getModuleId())) {
					returnProcessStepHandlerList.add(fileTypeHandler);
					break;
				}
			}
		}
		return returnProcessStepHandlerList;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IFileTypeHandler> myMap = context.getBeansOfType(IFileTypeHandler.class);
		for (Map.Entry<String, IFileTypeHandler> entry : myMap.entrySet()) {
			IFileTypeHandler typeHandler = entry.getValue();
			componentMap.put(typeHandler.getName().toUpperCase(), typeHandler);
			FileTypeVo fileTypeVo = new FileTypeVo();
			fileTypeVo.setName(typeHandler.getName());
			fileTypeVo.setDisplayName(typeHandler.getDisplayName());
			fileTypeVo.setModuleId(context.getId());
			fileTypeList.add(fileTypeVo);
		}
	}
}
