/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.integration.dto.IntegrationHandlerVo;

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
    protected void onInitialized(CodedriverWebApplicationContext context) {
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
