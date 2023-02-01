/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
