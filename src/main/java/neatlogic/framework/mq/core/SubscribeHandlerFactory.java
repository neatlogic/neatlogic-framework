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
import neatlogic.framework.mq.dto.SubscribeHandlerVo;
import neatlogic.framework.mq.dto.SubscribeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.*;

@RootComponent
public class SubscribeHandlerFactory extends ModuleInitializedListenerBase implements BeanFactoryPostProcessor {
    private static final Map<String, ISubscribeHandler> componentMap = new HashMap<>();
    private static final Map<String, SubscribeVo> systemSubscribeMap = new LinkedHashMap<>();

    public static ISubscribeHandler getHandler(String handlerId) {
        return componentMap.get(handlerId);
    }

    private static final List<SubscribeHandlerVo> subscribeHandlerVoList = new ArrayList<>();

    public static List<SubscribeHandlerVo> getSubscribeHandlerList() {
        return subscribeHandlerVoList;
    }

    public static List<SubscribeVo> getSystemSubscribeList() {
        return List.copyOf(systemSubscribeMap.values());
    }

    public static boolean hasSystemSubscribe(String name) {
        return StringUtils.isNotBlank(name) && systemSubscribeMap.containsKey(name);
    }

    public static boolean isEmbedSubscribeHandler(String className) {
        if (StringUtils.isBlank(className)) {
            return false;
        }
        return subscribeHandlerVoList.stream().anyMatch(o -> className.equals(o.getClassName()) && Boolean.TRUE.equals(o.getIsEmbed()));
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, ISubscribeHandler> myMap = context.getBeansOfType(ISubscribeHandler.class);
        for (Map.Entry<String, ISubscribeHandler> entry : myMap.entrySet()) {
            ISubscribeHandler component = entry.getValue();
            if(componentMap.containsKey(component.getClassName())) {
                throw new RuntimeException("Duplicate subscribe handler: " + component.getClassName());
            }
            componentMap.put(component.getClassName(), component);
            boolean isEmbed = false;
            List<SubscribeVo> systemSubscribeList = component.getSystemSubscribeList();
            if (CollectionUtils.isNotEmpty(systemSubscribeList)) {
                for (SubscribeVo subscribeVo : systemSubscribeList) {
                    if (subscribeVo == null || StringUtils.isBlank(subscribeVo.getName())) {
                        continue;
                    }
                    systemSubscribeMap.put(subscribeVo.getName(), subscribeVo);
                    isEmbed = true;
                }
            }
            subscribeHandlerVoList.add(new SubscribeHandlerVo(component.getName(), component.getLabel(), component.getClassName(), isEmbed));
        }
    }

    @Override
    protected void myInit() {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
