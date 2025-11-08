/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.mq.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.mq.dto.TopicVo;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@RootComponent
public class TopicFactory extends ModuleInitializedListenerBase {
    private static final Map<String, ITopic> componentMap = new HashMap<>();
    private static final List<TopicVo> topicList = new ArrayList<>();

    public static ITopic getTopic(String topicName) {
        return componentMap.get(topicName);
    }

    public static List<TopicVo> getTopicList(String handler) {
        if (StringUtils.isNotBlank(handler)) {
            return topicList.stream().filter(d -> d.getHandler().equals(handler)).collect(Collectors.toList());
        }
        return topicList;
    }

    public static TopicVo getTopicByName(String topicName) {
        Optional<TopicVo> op = topicList.stream().filter(t -> t.getName().equals(topicName)).findFirst();
        if (op.isPresent()) {
            op.get().setIsActive(1);
            return op.get();
        }
        return null;
    }

    public static boolean hasTopic(String topicName) {
        return topicList.stream().anyMatch(d -> d.getName().equals(topicName));
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, ITopic> myMap = context.getBeansOfType(ITopic.class);
        for (Map.Entry<String, ITopic> entry : myMap.entrySet()) {
            ITopic component = entry.getValue();
            if (component.getName() != null) {
                componentMap.put(component.getName(), component);
                TopicVo topicVo = new TopicVo();
                topicVo.setName(component.getName());
                topicVo.setLabel(component.getLabel());
                topicVo.setDescription(component.getDescription());
                topicVo.setHandler(component.getHandler());
                topicVo.setIsActive(1);
                topicList.add(topicVo);
            }
        }
        topicList.sort(Comparator.comparing(TopicVo::getName));
    }

    @Override
    protected void myInit() {

    }
}
