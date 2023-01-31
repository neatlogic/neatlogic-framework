/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.mq.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.mq.dto.TopicVo;

import java.util.*;

@RootComponent
public class TopicFactory extends ModuleInitializedListenerBase {
    private static final Map<String, ITopic> componentMap = new HashMap<>();
    private static final List<TopicVo> topicList = new ArrayList<>();

    public static ITopic getTopic(String topicName) {
        return componentMap.get(topicName);
    }

    public static List<TopicVo> getTopicList() {
        return topicList;
    }

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, ITopic> myMap = context.getBeansOfType(ITopic.class);
        for (Map.Entry<String, ITopic> entry : myMap.entrySet()) {
            ITopic component = entry.getValue();
            if (component.getName() != null) {
                componentMap.put(component.getName(), component);
                TopicVo topicVo = new TopicVo();
                topicVo.setName(component.getName());
                topicVo.setLabel(component.getLabel());
                topicVo.setDescription(component.getDescription());
                topicList.add(topicVo);
            }
        }
        topicList.sort(Comparator.comparing(TopicVo::getName));
    }

    @Override
    protected void myInit() {

    }
}
