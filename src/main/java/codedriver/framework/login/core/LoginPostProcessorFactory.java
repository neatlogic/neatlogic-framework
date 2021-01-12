package codedriver.framework.login.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.plutext.jaxb.xslfo.ForcePageCountType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Title: LoginPostProcessorFactory
 * @Package codedriver.framework.login.core
 * @Description: 登录后处理器工厂类
 * @Author: linbq
 * @Date: 2021/1/6 15:06
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@RootComponent
public class LoginPostProcessorFactory extends ApplicationListenerBase {

    private static Set<ILoginPostProcessor> loginPostProcessorSet = new HashSet<>();

    public static Set<ILoginPostProcessor> getLoginPostProcessorSet(){
        return loginPostProcessorSet;
    }
    @Override
    protected void myInit() {

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, ILoginPostProcessor> map = context.getBeansOfType(ILoginPostProcessor.class);
        for (Map.Entry<String, ILoginPostProcessor> entry : map.entrySet()){
            loginPostProcessorSet.add(entry.getValue());
        }
    }
}
