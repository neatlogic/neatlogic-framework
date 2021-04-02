/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 依赖关系处理器工厂
 * @author: linbq
 * @since: 2021/4/1 11:13
 **/
@RootComponent
public class DependencyHandlerFactory extends ApplicationListenerBase {

    private static Map<String, IDependencyHandler> componentMap = new HashMap<>();

    private static Map<ICalleeType, List<IDependencyHandler>> calleeHandlerListMap = new HashMap<>();

    public static IDependencyHandler getHandler(String handler) {
        return componentMap.get(handler);
    }

    public static List<IDependencyHandler> getHandlerList(ICalleeType calleeType) {
        return calleeHandlerListMap.get(calleeType);
    }

    @Override
    protected void myInit() {

    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IDependencyHandler> myMap = context.getBeansOfType(IDependencyHandler.class);
        for (Map.Entry<String, IDependencyHandler> entry : myMap.entrySet()) {
            IDependencyHandler component = entry.getValue();
            if (componentMap.containsKey(component.getHandler())) {
                throw new RuntimeException("处理器：" + component.getHandler() + "已存在，请修改代码");
            }
            componentMap.put(component.getHandler(), component);
            calleeHandlerListMap.computeIfAbsent(component.getCalleeType(), k -> new ArrayList<>()).add(component);
        }
    }
}
