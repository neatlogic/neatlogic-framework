/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.attribute.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.*;

@RootComponent
public class FormAttributeHandlerFactory extends ApplicationListenerBase {
    private static final Map<String, IFormAttributeHandler> handlerMap = new HashMap<>();
    private static final List<IFormAttributeHandler> handlerList = new ArrayList<>();

    public static IFormAttributeHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    public static List<IFormAttributeHandler> getHandlerList() {
        handlerList.sort(Comparator.comparingInt(IFormAttributeHandler::getSort));
        return handlerList;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IFormAttributeHandler> myMap = context.getBeansOfType(IFormAttributeHandler.class);
        for (Map.Entry<String, IFormAttributeHandler> entry : myMap.entrySet()) {
            IFormAttributeHandler handler = entry.getValue();
            if (handler.getHandler() != null) {
                if (handlerMap.containsKey(handler.getHandler())) {
                    System.err.println("表单插件：" + handler.getHandler() + "已存在，请检查代码");
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
