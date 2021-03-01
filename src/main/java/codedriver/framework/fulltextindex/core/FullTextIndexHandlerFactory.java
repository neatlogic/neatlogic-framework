package codedriver.framework.fulltextindex.core;

import codedriver.framework.common.RootComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class FullTextIndexHandlerFactory implements ApplicationListener<ContextRefreshedEvent> {
    private static final Map<IFullTextIndexType, IFullTextIndexHandler> componentMap = new HashMap<>();
    private static final List<IFullTextIndexType> typeList = new ArrayList<>();


    public static List<IFullTextIndexType> getTypeList() {
        return typeList;
    }

    public static IFullTextIndexHandler getComponent(IFullTextIndexType type) {
        if (!componentMap.containsKey(type) || componentMap.get(type) == null) {
            throw new RuntimeException("找不到类型为：" + type.getTypeName() + "的全文索引组件");
        }
        return componentMap.get(type);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IFullTextIndexHandler> myMap = context.getBeansOfType(IFullTextIndexHandler.class);
        for (Map.Entry<String, IFullTextIndexHandler> entry : myMap.entrySet()) {
            IFullTextIndexHandler component = entry.getValue();
            if (component.getType() != null) {
                componentMap.put(component.getType(), component);
                if (!typeList.contains(component.getType())) {
                    typeList.add(component.getType());
                }
            }
        }
    }
}
