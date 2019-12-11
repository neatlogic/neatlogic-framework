package codedriver.framework.counter;

import codedriver.framework.common.RootComponent;

import codedriver.framework.counter.dao.mapper.GlobalCounterMapper;
import codedriver.framework.counter.dto.GlobalCounterVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: balantflow
 * @description: 系统通知工厂
 * @create: 2019-08-08 16:02
 **/
@RootComponent
public class GlobalCounterFactory implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private GlobalCounterMapper counterMapper;

    private static Map<String, IGlobalCounter> counterMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(GlobalCounterFactory.class);

    public static IGlobalCounter getCounter(String name){
        if (!counterMap.containsKey(name)){
            throw new RuntimeException("找不到名称为：" + name +" 的消息统计插件");
        }
        return counterMap.get(name);
    }

    @PostConstruct
    private void resetIsActiveOfAllCounterPlugin(){
        counterMapper.resetIsActiveOfAllCounter();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IGlobalCounter> map = context.getBeansOfType(IGlobalCounter.class);
        for (Map.Entry<String, IGlobalCounter> entry : map.entrySet()){
        	IGlobalCounter counter = entry.getValue();
            try {
                GlobalCounterVo counterVo = new GlobalCounterVo();
                counterVo.setName(counter.getName());
                counterVo.setModuleId(context.getId());
                counterVo.setDescription(counter.getDescription());
                counterVo.setPluginId(counter.getPluginId());
                counterVo.setIsActive(1);

                if (counterMapper.getCounterCountByPluginId(counter.getPluginId()) < 1){
                    counterMapper.insertCounter(counterVo);
                }else {
                    counterMapper.updateCounterByPluginId(counterVo);
                }

                counterMap.put(counter.getPluginId(), counter);
            } catch (Exception e) {
                logger.error("消息统计插件：" + counter.getName() + "加载失败," + e.getMessage());
            }
        }
    }
}
