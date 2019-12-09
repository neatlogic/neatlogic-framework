package codedriver.framework.inform.core;

import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 10:04
 **/
@Component
public class InformComponentFactory implements ApplicationListener<ContextRefreshedEvent> {
    private static Map<String, InformComponentBase> informPluginMap = new HashMap<>();

    public static InformComponentBase getInformPlugin(String informPluginType){
        if (informPluginType.contains(informPluginType)){
            return informPluginMap.get(informPluginType);
        }
        throw new RuntimeException("插件不存在");
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
