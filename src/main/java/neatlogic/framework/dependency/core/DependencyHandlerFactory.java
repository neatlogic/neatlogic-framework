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

package neatlogic.framework.dependency.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 依赖关系处理器工厂
 *
 * @author: linbq
 * @since: 2021/4/1 11:13
 **/
@RootComponent
public class DependencyHandlerFactory extends ModuleInitializedListenerBase {

    private static final Map<String, IDependencyHandler> componentMap = new HashMap<>();

    private static final Map<IFromType, List<IDependencyHandler>> calleeHandlerListMap = new HashMap<>();

    /**
     * 通过handler获取依赖关系处理器对象
     *
     * @param handler 依赖关系处理器唯一标识
     */
    public static IDependencyHandler getHandler(String handler) {
        return componentMap.get(handler);
    }

    /**
     * 通过calleeType获取依赖关系处理器对象列表，一个功能可以被多个地方引用，例如集成可以被流程图的动作引用，也可以被矩阵外部数据源引用
     *
     * @param fromType 被调用者类型
     */
    public static List<IDependencyHandler> getHandlerList(IFromType fromType) {
        return calleeHandlerListMap.get(fromType);
    }

    @Override
    protected void myInit() {

    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        String groupName = context.getGroupName();
        Map<String, IDependencyHandler> myMap = context.getBeansOfType(IDependencyHandler.class);
        for (Map.Entry<String, IDependencyHandler> entry : myMap.entrySet()) {
            IDependencyHandler component = entry.getValue();
            component.setGroupName(groupName);
            componentMap.put(component.getHandler(), component);
            calleeHandlerListMap.computeIfAbsent(component.getFromType(), k -> new ArrayList<>()).add(component);
        }
    }
}
