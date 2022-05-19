/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.globallock.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class GlobalLockHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IGlobalLockHandler> handlerMap = new HashMap<>();
    private static final List<IGlobalLockHandler> handlerList = new ArrayList<>();

    public static IGlobalLockHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    public static List<IGlobalLockHandler> getHandlerList() {
        return handlerList;
    }

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IGlobalLockHandler> myMap = context.getBeansOfType(IGlobalLockHandler.class);
        for (Map.Entry<String, IGlobalLockHandler> entry : myMap.entrySet()) {
            IGlobalLockHandler handler = entry.getValue();
            if (handler.getHandler() != null) {
                if (handlerMap.containsKey(handler.getHandler())) {
                    System.err.println("global lock handler：" + handler.getHandler() + "已存在，请检查代码");
                } else {
                    handlerMap.put(handler.getHandler(), handler);
                    handlerList.add(handler);
                }
            }
        }
    }

    @Override
    protected void myInit() {

    }
}