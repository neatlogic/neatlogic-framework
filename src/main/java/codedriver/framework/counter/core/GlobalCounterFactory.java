package codedriver.framework.counter.core;

import codedriver.framework.common.RootComponent;

import codedriver.framework.counter.dto.GlobalCounterVo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: balantflow
 * @description: 系统通知工厂
 * @create: 2019-08-08 16:02
 **/
@RootComponent
public class GlobalCounterFactory implements ApplicationListener<ContextRefreshedEvent> {

    private static Map<String, IGlobalCounter> counterMap = new HashMap<>();
    private static List<GlobalCounterVo> counterVoList = new ArrayList<>();

    public static IGlobalCounter getCounter(String pluginId){
        if (!counterMap.containsKey(pluginId)){
            throw new RuntimeException("找不到ID为：" + pluginId +" 的消息统计插件");
        }
        return counterMap.get(pluginId);
    }

    public static List<GlobalCounterVo> getCounterVoList(){
        return counterVoList;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IGlobalCounter> map = context.getBeansOfType(IGlobalCounter.class);
        for (Map.Entry<String, IGlobalCounter> entry : map.entrySet()){
        	IGlobalCounter counter = entry.getValue();
            GlobalCounterVo counterVo = new GlobalCounterVo();
            counterVo.setName(counter.getName());
            counterVo.setModuleId(context.getId());
            counterVo.setDescription(counter.getDescription());
            counterVo.setPluginId(counter.getPluginId());
            counterVoList.add(counterVo);
            counterMap.put(counter.getPluginId(), counter);

        }
    }
}
