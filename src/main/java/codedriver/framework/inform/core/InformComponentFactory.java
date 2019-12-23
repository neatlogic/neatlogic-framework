package codedriver.framework.inform.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.common.RootComponent;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 10:04
 **/
@RootComponent
public class InformComponentFactory implements ApplicationListener<ContextRefreshedEvent> {
    private static Map<String, InformComponentBase> informPluginMap = new HashMap<>();

    public static InformComponentBase getInformPlugin(String informPluginType){
        if (informPluginType.contains(informPluginType)){
            return informPluginMap.get(informPluginType);
        }
        throw new RuntimeException("找不到类型为：" + informPluginType + "的流程组件");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, InformComponentBase> myMap = context.getBeansOfType(InformComponentBase.class);
        for (Map.Entry<String, InformComponentBase> entry : myMap.entrySet()){
            InformComponentBase plugin = entry.getValue();
            if (plugin.getId() != null){
                informPluginMap.put(plugin.getId(), plugin);
            }
        }
    }
}
