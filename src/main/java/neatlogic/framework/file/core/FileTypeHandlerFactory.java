/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
