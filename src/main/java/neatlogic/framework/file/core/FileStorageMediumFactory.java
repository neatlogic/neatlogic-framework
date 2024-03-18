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
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class FileStorageMediumFactory extends ModuleInitializedListenerBase {

    private static final Map<String, IFileStorageHandler> componentMap = new HashMap<>();

    public static IFileStorageHandler getHandler(String storageMedium) {
        storageMedium = storageMedium.toUpperCase();
        return componentMap.get(storageMedium);
    }


    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IFileStorageHandler> myMap = context.getBeansOfType(IFileStorageHandler.class);
        for (Map.Entry<String, IFileStorageHandler> entry : myMap.entrySet()) {
            IFileStorageHandler fileStorageMediumHandler = entry.getValue();
            componentMap.put(fileStorageMediumHandler.getName().toUpperCase(), fileStorageMediumHandler);
        }
    }

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub
		
	}
}
