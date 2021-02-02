package codedriver.framework.notify.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: BuildNotifyContentHandlerFactory
 * @Package: codedriver.framework.notify.core
 * @Description:
 * @Author: laiwt
 * @Date: 2021/1/25 16:59
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@RootComponent
public class BuildNotifyContentHandlerFactory extends ApplicationListenerBase {

    private static Map<String, IBuildNotifyContentHandler> buildNotifyVoHandlerMap = new HashMap();

    public static IBuildNotifyContentHandler getHandler(String handler){
        return buildNotifyVoHandlerMap.get(handler);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IBuildNotifyContentHandler> myMap = context.getBeansOfType(IBuildNotifyContentHandler.class);
        for (Map.Entry<String, IBuildNotifyContentHandler> entry : myMap.entrySet()) {
            buildNotifyVoHandlerMap.put(entry.getValue().getNotifyHandlerClassName()
                            + "," + entry.getValue().getNotifyContentHandlerClassName(),entry.getValue());
        }
    }

    @Override
    protected void myInit() {

    }
}
