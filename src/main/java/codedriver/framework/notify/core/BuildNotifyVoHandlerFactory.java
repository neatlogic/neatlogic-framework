package codedriver.framework.notify.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: BuildNotifyVoHandlerFactory
 * @Package: codedriver.framework.notify.core
 * @Description:
 * @Author: laiwt
 * @Date: 2021/1/25 16:59
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@RootComponent
public class BuildNotifyVoHandlerFactory extends ApplicationListenerBase {

    private static Map<Map<String,String>,IBuildNotifyVoHandler> buildNotifyVoHandlerMap = new HashMap();

    public static IBuildNotifyVoHandler getHandler(Map<String,String> map){
        return buildNotifyVoHandlerMap.get(map);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IBuildNotifyVoHandler> myMap = context.getBeansOfType(IBuildNotifyVoHandler.class);
        for (Map.Entry<String, IBuildNotifyVoHandler> entry : myMap.entrySet()) {
            Map<String,String> map = new HashMap<>();
            map.put(entry.getValue().getNotifyHandlerClassName(),entry.getValue().getNotifyContentHandlerClassName());
            buildNotifyVoHandlerMap.put(map,entry.getValue());
        }
    }

    @Override
    protected void myInit() {

    }
}
