/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.treeselect.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class TreeSelectDataSourceFactory extends ApplicationListenerBase {
    private static final Map<String, ITreeSelectDataSourceHandler> handlerMap = new HashMap<>();
    private static final List<ITreeSelectDataSourceHandler> handlerList = new ArrayList<>();

    public static ITreeSelectDataSourceHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    public static List<ITreeSelectDataSourceHandler> getHandlerList() {
        return handlerList;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, ITreeSelectDataSourceHandler> myMap = context.getBeansOfType(ITreeSelectDataSourceHandler.class);
        for (Map.Entry<String, ITreeSelectDataSourceHandler> entry : myMap.entrySet()) {
            ITreeSelectDataSourceHandler handler = entry.getValue();
            if (handler.getHandler() != null) {
                if (handlerMap.containsKey(handler.getHandler())) {
                    System.err.println("树形下拉表单数据源插件：" + handler.getHandler() + "已存在，请检查代码");
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