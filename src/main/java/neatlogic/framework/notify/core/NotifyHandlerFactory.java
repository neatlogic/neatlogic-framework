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

package neatlogic.framework.notify.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.dto.ValueTextVo;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class NotifyHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, INotifyHandler> notifyHandlerMap = new HashMap<>();

//    private static final List<ValueTextVo> notifyHandlerTypeList = new ArrayList<>();
//
//    private static final List<ValueTextVo> notifyHandlerNameList = new ArrayList<>();

    public static List<ValueTextVo> getNotifyHandlerTypeList() {
        List<ValueTextVo> resultList = new ArrayList<>();
        for (Map.Entry<String, INotifyHandler> entry : notifyHandlerMap.entrySet()) {
            INotifyHandler notifyHandler = entry.getValue();
            resultList.add(new ValueTextVo(ClassUtils.getUserClass(notifyHandler.getClass()).getSimpleName(), notifyHandler.getName()));
        }
        return resultList;
    }

    public static List<ValueTextVo> getNotifyHandlerNameList() {
        List<ValueTextVo> resultList = new ArrayList<>();
        for (Map.Entry<String, INotifyHandler> entry : notifyHandlerMap.entrySet()) {
            INotifyHandler notifyHandler = entry.getValue();
            resultList.add(new ValueTextVo(ClassUtils.getUserClass(notifyHandler.getClass()).getSimpleName(), notifyHandler.getType()));
        }
        return resultList;
    }

    public static INotifyHandler getHandler(String handler) {
        return notifyHandlerMap.get(handler);
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, INotifyHandler> myMap = context.getBeansOfType(INotifyHandler.class);
        for (Map.Entry<String, INotifyHandler> entry : myMap.entrySet()) {
            INotifyHandler plugin = entry.getValue();
            if (plugin.getClassName() != null) {
                notifyHandlerMap.put(ClassUtils.getUserClass(plugin.getClass()).getSimpleName(), plugin);
//                notifyHandlerTypeList.add(new ValueTextVo(plugin.getClass().getSimpleName(), plugin.getName()));
//                notifyHandlerNameList.add(new ValueTextVo(plugin.getClass().getSimpleName(), plugin.getType()));
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
