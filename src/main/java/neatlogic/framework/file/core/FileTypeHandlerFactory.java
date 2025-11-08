/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.file.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.file.dto.FileTypeVo;

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

	public static FileTypeVo getActiveFileTypeHandlerByType(String type) {
		TenantContext tenantContext = TenantContext.get();
		List<ModuleVo> moduleList = tenantContext.getActiveModuleList();
		List<FileTypeVo> returnProcessStepHandlerList = new ArrayList<>();
		for (FileTypeVo fileTypeHandler : fileTypeList) {
			for (ModuleVo moduleVo : moduleList) {
				if (moduleVo.getId().equalsIgnoreCase(fileTypeHandler.getModuleId())) {
					if(fileTypeHandler.getName().equalsIgnoreCase(type)) {
						return fileTypeHandler;
					}
					break;
				}
			}
		}
		return null;
	}

	@Override
	public void onInitialized(NeatLogicWebApplicationContext context) {
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
