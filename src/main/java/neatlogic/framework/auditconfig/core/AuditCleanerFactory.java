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
