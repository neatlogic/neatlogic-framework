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
