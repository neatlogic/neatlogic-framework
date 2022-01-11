/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;

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
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IDependencyHandler> myMap = context.getBeansOfType(IDependencyHandler.class);
        for (Map.Entry<String, IDependencyHandler> entry : myMap.entrySet()) {
            IDependencyHandler component = entry.getValue();
            componentMap.put(component.getHandler(), component);
            calleeHandlerListMap.computeIfAbsent(component.getFromType(), k -> new ArrayList<>()).add(component);
        }
    }
}
