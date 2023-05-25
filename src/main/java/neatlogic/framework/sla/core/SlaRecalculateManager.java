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

package neatlogic.framework.sla.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class SlaRecalculateManager extends ModuleInitializedListenerBase {

    private static final Map<String, ISlaRecalculateHandler> map = new HashMap<>();

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, ISlaRecalculateHandler> myMap = context.getBeansOfType(ISlaRecalculateHandler.class);
        for (Map.Entry<String, ISlaRecalculateHandler> entry : myMap.entrySet()) {
            ISlaRecalculateHandler handler = entry.getValue();
            map.put(handler.getHandler(), handler);
        }
    }

    @Override
    protected void myInit() {

    }

    public static void execute(String worktimeUuid) {
        for (Map.Entry<String, ISlaRecalculateHandler> entry : map.entrySet()) {
            ISlaRecalculateHandler handler = entry.getValue();
            handler.execute(worktimeUuid);
        }
    }
}
