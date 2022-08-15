/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.lcs.linehandler.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RootComponent
public class LineHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, ILineHandler> handlerMap = new HashMap<>();
    private static final List<ILineHandler> handlerList = new ArrayList<>();

    public static ILineHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    public static List<ILineHandler> getHandlerList() {
        return handlerList;
    }

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, ILineHandler> myMap = context.getBeansOfType(ILineHandler.class);
        for (Map.Entry<String, ILineHandler> entry : myMap.entrySet()) {
            ILineHandler handler = entry.getValue();
            if (handler.getHandler() != null) {
                if (handlerMap.containsKey(handler.getHandler())) {
                    System.err.println("知识插件：" + handler.getHandler() + "已存在，请检查代码");
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
