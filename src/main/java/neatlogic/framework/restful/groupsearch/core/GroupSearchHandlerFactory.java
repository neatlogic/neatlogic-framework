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

package neatlogic.framework.restful.groupsearch.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.exception.groupsearch.GroupSearchHandlerNotFoundException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class GroupSearchHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IGroupSearchHandler> componentMap = new HashMap<>();

    public static IGroupSearchHandler getHandler(String handler) {
        if (!componentMap.containsKey(handler) || componentMap.get(handler) == null) {
            throw new GroupSearchHandlerNotFoundException(handler);
        }
        return componentMap.get(handler);
    }

    public static Map<String, IGroupSearchHandler> getComponentMap() {
        return componentMap;
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IGroupSearchHandler> myMap = context.getBeansOfType(IGroupSearchHandler.class);
        for (Map.Entry<String, IGroupSearchHandler> entry : myMap.entrySet()) {
            IGroupSearchHandler component = entry.getValue();
            if (StringUtils.isNotBlank(component.getName())) {
                componentMap.put(component.getName(), component);
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
