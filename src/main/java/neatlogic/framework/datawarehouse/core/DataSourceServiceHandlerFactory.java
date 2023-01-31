/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.datawarehouse.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class DataSourceServiceHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IDataSourceServiceHandler> handlerMap = new HashMap<>();
    private static final List<IDataSourceServiceHandler> handlerList = new ArrayList<>();

    public static IDataSourceServiceHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    public static List<IDataSourceServiceHandler> getHandlerList() {
        return handlerList;
    }

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IDataSourceServiceHandler> myMap = context.getBeansOfType(IDataSourceServiceHandler.class);
        for (Map.Entry<String, IDataSourceServiceHandler> entry : myMap.entrySet()) {
            IDataSourceServiceHandler handler = entry.getValue();
            if (handler.getHandler() != null) {
                if (handlerMap.containsKey(handler.getHandler())) {
                    System.err.println("datasource handler：" + handler.getHandler() + "已存在，请检查代码");
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
