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
