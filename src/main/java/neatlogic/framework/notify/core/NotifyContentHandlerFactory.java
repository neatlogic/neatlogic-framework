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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@RootComponent
public class NotifyContentHandlerFactory extends ModuleInitializedListenerBase {

    private static final List<ValueTextVo> notifyContentHandlerList = new ArrayList<>();

    private static final Map<String, INotifyContentHandler> notifyContentHandlerMap = new HashMap<>();

    public static INotifyContentHandler getHandler(String handler) {
        return notifyContentHandlerMap.get(handler);
    }

    public static List<ValueTextVo> getNotifyContentHandlerList() {
        return notifyContentHandlerList;
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, INotifyContentHandler> map = context.getBeansOfType(INotifyContentHandler.class);
        for (Entry<String, INotifyContentHandler> entry : map.entrySet()) {
            notifyContentHandlerMap.put(entry.getValue().getClassName(), entry.getValue());
            notifyContentHandlerList.add(new ValueTextVo(entry.getValue().getClassName(), entry.getValue().getName()));
        }
    }

    @Override
    protected void myInit() {

    }

}
