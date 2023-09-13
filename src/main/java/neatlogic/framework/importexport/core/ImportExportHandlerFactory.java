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

package neatlogic.framework.importexport.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class ImportExportHandlerFactory extends ModuleInitializedListenerBase {

    private Logger logger = LoggerFactory.getLogger(ImportExportHandlerFactory.class);

    private static Map<String, ImportExportHandler> componentMap = new HashMap<>();

    public static ImportExportHandler getHandler(String handler) {
        return componentMap.get(handler);
    }

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, ImportExportHandler> myMap = context.getBeansOfType(ImportExportHandler.class);
        for (Map.Entry<String, ImportExportHandler> entry : myMap.entrySet()) {
            ImportExportHandler component = entry.getValue();
            if (component.getType() == null) {
                logger.error("ImportExportHandler '" + component.getClass().getSimpleName() + "' type is null");
                System.exit(1);
            }
            if (componentMap.containsKey(component.getType().getValue())) {
                logger.error("ImportExportHandler '" + component.getClass().getSimpleName() + "(" + component.getType().getValue() + ")' repeat");
                System.exit(1);
            }
            componentMap.put(component.getType().getValue(), component);
        }
    }

    @Override
    protected void myInit() {

    }
}
