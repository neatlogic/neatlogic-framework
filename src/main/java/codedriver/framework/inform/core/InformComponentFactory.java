package codedriver.framework.inform.core;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 10:04
 **/
public class InformComponentFactory {
    private static Map<String, InformComponentBase> informPluginMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("codedriver");
        Set<Class<? extends InformComponentBase>> pluginSet = reflections.getSubTypesOf(InformComponentBase.class);
        for (Class<? extends InformComponentBase> c : pluginSet){
            try {
                InformComponentBase executor = c.newInstance();
                informPluginMap.put(executor.getId(), executor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static InformComponentBase getInformPlugin(String informPluginType){
        if (informPluginType.contains(informPluginType)){
            return informPluginMap.get(informPluginType);
        }
        throw new RuntimeException("插件不存在");
    }
}
