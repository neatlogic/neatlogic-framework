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

package neatlogic.framework.auditconfig.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class AuditCleanerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IAuditCleaner> cleanerMap = new HashMap<>();

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IAuditCleaner> myMap = context.getBeansOfType(IAuditCleaner.class);
        for (Map.Entry<String, IAuditCleaner> entry : myMap.entrySet()) {
            IAuditCleaner component = entry.getValue();
            cleanerMap.put(component.getName(), component);
        }
    }

    public static IAuditCleaner getCleaner(String name) {
        return cleanerMap.get(name);
    }

    @Override
    protected void myInit() {

    }
}
