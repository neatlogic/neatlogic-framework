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

package neatlogic.framework.integration.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.integration.dto.IntegrationHandlerVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class IntegrationHandlerFactory extends ModuleInitializedListenerBase {

    private static final Map<String, IIntegrationHandler> componentMap = new HashMap<>();
    private static final List<IntegrationHandlerVo> handlerList = new ArrayList<>();

    public static IIntegrationHandler getHandler(String handler) {
        return componentMap.get(handler);
    }

    public static IIntegrationHandler getHandler(IIntegrationHandler handler) {
        return componentMap.get(handler.getHandler());
    }

    public static List<IntegrationHandlerVo> getHandlerList() {
        return handlerList;
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IIntegrationHandler> myMap = context.getBeansOfType(IIntegrationHandler.class);
        for (Map.Entry<String, IIntegrationHandler> entry : myMap.entrySet()) {
            IIntegrationHandler component = entry.getValue();
            if (component.getHandler() != null) {
                componentMap.put(component.getHandler(), component);
                handlerList.add(new IntegrationHandlerVo(component.getName(), component.getHandler()));
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
