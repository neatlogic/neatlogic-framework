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
