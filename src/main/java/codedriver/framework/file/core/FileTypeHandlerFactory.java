/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dto.module.ModuleVo;
import codedriver.framework.file.dto.FileTypeVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class FileTypeHandlerFactory extends ModuleInitializedListenerBase {

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
	public void onInitialized(CodedriverWebApplicationContext context) {
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

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub
		
	}
}
